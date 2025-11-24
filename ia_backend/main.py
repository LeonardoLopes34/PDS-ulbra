import base64
import json
import os
import chromadb
from fastapi import FastAPI, HTTPException, UploadFile, File
from pydantic import BaseModel
from openai import OpenAI
from dotenv import load_dotenv
import datetime
from typing import Annotated, Optional

load_dotenv()

client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))


chroma_client = chromadb.PersistentClient(path="./chroma.db")

expense_collection = chroma_client.get_or_create_collection(name="expenses")

app = FastAPI()

class Expense(BaseModel):
    id: int
    value: float
    description: str
    category_name: str
    payment_method: str
    date: str

class SyncRequest(BaseModel):
    expense: Expense

class AiFilterRequest(BaseModel):
    prompt: str

class BulkSyncRequest(BaseModel):
    expenses: list[Expense]

class ReceiptAnalysisResponse(BaseModel):
    total_value: float
    description: str
    suggested_category: str
    date: str | None = None
    payment_method: Optional[str] = None

class ReceiptData(BaseModel):
    total_value: float
    description: str
    suggested_category: str
    payment_method: Optional[str] = None
    date: Optional[str] = None

class InsightsResponse(BaseModel):
    insight_text: str

def get_embedding(text: str) -> list[float]:
    response = client.embeddings.create(
        model="text-embedding-3-small",
        input=text
    )
    return response.data[0].embedding

@app.post("/bulk-sync-expenses")
async def bulk_sync_expenses_endpoint(request: BulkSyncRequest):
    if not request.expenses:
        return {"status": "no expenses provided"}

    ids_list = []
    documents_list = []
    
    for expense in request.expenses:
        expense_text = (
            f"Gasto de R${expense.value:.2f} na categoria '{expense.category_name}' em {expense.date}. "
            f"Descrição: {expense.description}. Pago com {expense.payment_method}."
        )
        ids_list.append(str(expense.id))
        documents_list.append(expense_text)
    
    try:
        response = client.embeddings.create(
            model="text-embedding-3-small",
            input=documents_list
        )
        
        embeddings_list = [data.embedding for data in response.data]
        
    except Exception as e:
        print(f"Erro ao chamar a API de Embeddings da OpenAI: {e}")
        raise HTTPException(status_code=500, detail="Erro ao processar embeddings")

    expense_collection.upsert(
        ids=ids_list,
        embeddings=embeddings_list,
        documents=documents_list
    )
    
    return {"status": "success", "synced_count": len(ids_list)}


@app.post("/sync-expense")
async def sync_expense_endpoint(request: SyncRequest):
    expense = request.expense
    expense_text = (
        f"Gasto de R${expense.value:.2f} na categoria '{expense.category_name}' em {expense.date}."
        f"Descrição: {expense.description}. Pago com {expense.payment_method}."
    )

    embedding = get_embedding(expense_text)
    
    expense_collection.upsert(
        ids=[str(expense.id)],
        embeddings=[embedding],
        documents=[expense_text],
        metadatas=[{"category_name": expense.category_name}]
    )
    return {"status": "success", "synced_id": expense.id}


@app.post("/filter-expenses")
async def ai_filter_endpoint(request: AiFilterRequest):
    prompt_embedding = get_embedding(request.prompt)

    results = expense_collection.query(
        query_embeddings=[prompt_embedding],
        n_results=10
    )

    context_documents = results['documents'][0] if results['documents'] else []

    system_prompt = """
    Você é um assistente financeiro amigável e proativo, chamado 'Meu Bolso'.
    Analise o Contexto (uma lista de despesas relevantes encontradas no banco de dados) e a Pergunta do Utilizador.

    Sua tarefa é dupla:
    1. Fornecer uma análise em texto (em 'analysis'). Esta análise deve:
       - Responder diretamente à pergunta do utilizador.
       - Se a pergunta for sobre gastos, calcular e mencionar o GASTO TOTAL dos itens encontrados.
       - Fornecer uma breve observação ou insight sobre esses gastos (ex: "Este foi o seu maior gasto no período" ou "Você teve X gastos nesta categoria").
       - Ser sempre em português brasileiro.
    2. Retornar os IDs das despesas do contexto que correspondem à pergunta (em 'ids').

    O seu formato de resposta DEVE ser um JSON válido assim:
    {
      "analysis": "Olá! Encontrei 3 gastos com 'Mercado' esta semana, totalizando R$ 245,50. O seu maior gasto foi...",
      "ids": [1, 5, 12]
    }
    """
    user_prompt = f"""
    Contexto das despesas relevantes:
    {chr(10).join(context_documents)}

    Pergunta do Utilizador: "{request.prompt}"

    Resposta:
    """

    response = client.chat.completions.create(
        model="gpt-4.1",
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt}
        ]
                )
    ai_response_text = response.choices[0].message.content

    try:
        data = json.loads(ai_response_text)
        analysis_text = data.get("analysis", "Não foi possível analisar.")
        ids = data.get("ids", [])
    except Exception as e:
        analysis_text = "Erro ao processar a resposta da IA."
        ids = []

    return {"analysis": analysis_text, "filteredExpenseIds": ids}


@app.post("/process-receipt", response_model=ReceiptAnalysisResponse)
async def process_receipt_endpoint(file: Annotated[UploadFile, File()]):
    try:
        image_data = await file.read()
        base64_image = base64.b64encode(image_data).decode("utf-8")
        today_date = datetime.date.today().isoformat()

        system_prompt = f"""
        Você é um assistente financeiro. Analise a imagem de um recibo.
        Extraia:
        - 'total_value' (float)
        - 'description' (string)
        - 'suggested_category' (string, ex: 'Mercado', 'Transporte')
        - 'payment_method' (string, ex: 'Pix', 'Dinheiro')
        - 'date' (string, formato 'YYYY-MM-DD')

        INFORMAÇÃO DE CONTEXTO IMPORTANTE: A data de hoje é {today_date}.
        Se o recibo não tiver um ano explícito (ex: '29/09'), assuma que o ano é o ano corrente ({today_date[:4]}).
        
        Responda APENAS com um objeto JSON.
        """
            
        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": [
                    {"type": "text", "text": "Analise este recibo e retorne o JSON."},
                    {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{base64_image}", "detail": "high"}},
                ]},
            ],
            response_format={"type": "json_object"}
        )
        
    
        ai_raw_content = response.choices[0].message.content
        if not ai_raw_content:
             raise ValueError("Resposta da IA veio vazia.")

        print(f"DEBUG: Resposta bruta da OpenAI: {ai_raw_content}")

        ai_json = json.loads(ai_raw_content)

        if not isinstance(ai_json, dict):
            raise ValueError(f"Resposta inesperada da IA: {ai_json}")

        ai_suggestion = ai_json.get("suggested_category", "Outros")
        prompt_embedding = get_embedding(ai_suggestion)
        results = expense_collection.query(
            query_embeddings=[prompt_embedding],
            n_results=1
        )
        categoria_refinada = ai_suggestion

        if (results['metadatas'] and results['metadatas'][0] and results['metadatas'][0][0]):
            metadata = results['metadatas'][0][0]
            categoria_refinada = metadata.get("category_name", ai_suggestion)

        return ReceiptAnalysisResponse(
            total_value=ai_json.get("total_value", 0.0),
            description=ai_json.get("description", "Descrição não encontrada"),
            suggested_category=categoria_refinada,
            payment_method=ai_json.get("payment_method"),
            date=ai_json.get("date")
        )

    except Exception as e:
        print(f"Erro ao processar recibo: {e}")
        raise HTTPException(status_code=500, detail=f"Erro no servidor de IA: {e}")
    

@app.post("/generate-insights", response_model=InsightsResponse)
async def generate_insights_endpoint():
    try: 
        search_terms = ["gastos com lazer", "gastos com alimentação", "gastos com transporte", "gastos com saúde", "gastos com moradia", "gastos com carro"]
        all_documents = []

        for term in search_terms:
            term_embedding = get_embedding(term)
            results = expense_collection.query(
                query_embeddings=[term_embedding],
                n_results=5
            )
            if results['documents'] and results['documents'][0]:
                all_documents.extend(results['documents'][0])

        unique_documents = list(set(all_documents))
        if not unique_documents:
            return InsightsResponse(insight_text="Não há dados suficientes para gerar insights no momento.")
        
        context_text = "\n".join(unique_documents)

        system_prompt = """
        Você é um consultor financeiro pessoal experiente, direto e amigável.
        Seu objetivo é analisar uma amostra dos gastos do usuário e fornecer 
        UM parágrafo curto com uma análise qualitativa (comportamental).
        
        Não tente somar valores exatos, pois você está vendo apenas uma amostra.
        Foque em:
        - Identificar categorias onde o usuário parece gastar muito (ex: muito delivery, muitas compras pequenas).
        - Dar um conselho prático para economizar.
        - Use emojis para tornar a leitura leve.
        - Fale diretamente com o usuário ("Você...").
        """
        
        user_prompt = f"""
        Analise esta amostra de gastos recentes:
        {context_text}
        
        Gere o insight financeiro:
        """

        response = client.chat.completions.create(
            model="gpt-4.1",
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt}
            ],
            temperature=0.7
        )
        insight_text = response.choices[0].message.content
        return InsightsResponse(insight_text=insight_text)
    except Exception as e:
        print(f"Erro ao gerar insights: {e}")
        raise HTTPException(status_code=500, detail="Erro ao gerar insights financeiros")
