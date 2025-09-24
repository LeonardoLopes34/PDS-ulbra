package com.example.controlegasto.presentation.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.domain.entities.PaymentMethod
import java.time.LocalDate

data class AdvancedFilterState(
    val selectedCategories: List<Category> = emptyList(),
    val selectedPaymentMethod: List<PaymentMethod> = emptyList(),
    val selectedDate: LocalDate
)

@Composable
fun AdvancedFilterDialog(
    onDismiss: () -> Unit,
    onApplyClick: (AdvancedFilterState) -> Unit,
    availableCategories: List<Category>,
    availablePaymentMethod: List<PaymentMethod>
) {
    val selectedCategories = remember { mutableStateListOf<Category>() }
    val selectedPaymentMethod = remember { mutableStateListOf<PaymentMethod>() }
    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtro Avançado", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
                ) {
                Text("Categorias", style = MaterialTheme.typography.titleSmall)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(availableCategories) {category ->
                        val isSelected = selectedCategories.contains(category)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable() {
                                    if (isSelected) {
                                        selectedCategories.remove(category)
                                    } else {
                                        selectedCategories.add(category)
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(category.name)
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(availablePaymentMethod) { paymentMethod ->
                        val isSelected = selectedPaymentMethod.contains(paymentMethod)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable() {
                                    if(isSelected) {
                                        selectedPaymentMethod.remove(paymentMethod)
                                    } else{
                                        selectedPaymentMethod.add(paymentMethod)
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(paymentMethod.displayName)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onApplyClick(
                    AdvancedFilterState(
                        selectedCategories = selectedCategories.toList(),
                        selectedPaymentMethod = selectedPaymentMethod.toList(),
                        selectedDate = selectedDate
                    )
                )
            }) { Text("Aplicar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}