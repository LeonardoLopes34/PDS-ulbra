package com.example.controlegasto.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.network.SyncExpensePayload
import com.example.controlegasto.data.network.SyncRequest
import com.example.controlegasto.domain.entities.Category
import java.time.format.DateTimeFormatter
import kotlin.math.exp

class SyncExpenseWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_EXPENSE_ID = "expense_id"
    }

    override suspend fun doWork(): Result {
        println("SyncExpenseWorker: O TRABALHO INICIOU.") // <-- LOG 1
        val expenseId = inputData.getInt(KEY_EXPENSE_ID, -1)
        if(expenseId == -1) {
            println("SyncExpenseWorker: ERRO! expenseId inválido.")
            return Result.failure()
        }

        println("SyncExpenseWorker: A sincronizar despesa com ID: $expenseId") // <-- LOG 2

        return try {
            val application = applicationContext as ExpenseControlApplication
            val expenseRepository = application.expenseRepository
            val categoryRepository = application.categoryRepository
            val apiService = application.apiService

            val expense = expenseRepository.getExpenseById(expenseId)

            if(expense != null ) {
                val category = categoryRepository.getCategoryById(expense.categoryId ?: 0) ?: Category.default()

                val payload = SyncExpensePayload(
                    id = expense.id,
                    value = expense.value.toFloat(),
                    description = expense.description,
                    categoryName = category.name,
                    paymentMethod = expense.paymentMethod?.displayName ?: "N/D",
                    date = expense.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
                val syncRequest = SyncRequest(expense = payload)

                println("SyncExpenseWorker: A enviar dados para o backend...") // <-- LOG 3
                apiService.syncExpense(syncRequest)
                println("SyncExpenseWorker: Sincronização CONCLUÍDA com sucesso.") // <-- LOG 4
                Result.success()
            } else {
                println("SyncExpenseWorker: ERRO! Despesa com ID $expenseId não encontrada no Room.")
                Result.failure()
            }
        } catch (e: Exception) {
            println("SyncExpenseWorker: ERRO NA CHAMADA DE REDE: ${e.message}") // <-- LOG 5
            Result.retry()
        }
    }

}