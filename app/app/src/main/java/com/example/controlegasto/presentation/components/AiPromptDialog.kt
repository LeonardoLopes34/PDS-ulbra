package com.example.controlegasto.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AiPromptDialog(
    onDismissRequest:() -> Unit,
    onApplyClick:(prompt: String) -> Unit
) {
    var promptText by remember { mutableStateOf("") }

    AlertDialog(
       onDismissRequest = onDismissRequest,
        title = {Text("Filtro Inteligente", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Descreva o que você procura. Ex: \"Gastos deste mês com mercado\"")
                OutlinedTextField(
                    value = promptText,
                    onValueChange = {promptText = it},
                    label = {Text("Seu pedido")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(100.dp),
                    placeholder = {Text("Quanto gastei com mercado nos ultimos 2 meses?")}
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyClick(promptText)
                },
                // O botão "Aplicar" só fica ativo se o utilizador digitar algo
                enabled = promptText.isNotBlank()
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}