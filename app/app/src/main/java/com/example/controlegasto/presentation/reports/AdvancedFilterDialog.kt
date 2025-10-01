package com.example.controlegasto.presentation.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.example.controlegasto.presentation.add_expense.PickerField
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


private enum class DatePickerTarget { START, END, NONE }

data class AdvancedFilterState(
    val selectedCategories: List<Category> = emptyList(),
    val selectedPaymentMethod: List<PaymentMethod> = emptyList(),
    val startDate: LocalDate,
    val endDate: LocalDate
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFilterDialog(
    onDismissRequest: () -> Unit,
    onApplyClick: (AdvancedFilterState) -> Unit,
    availableCategories: List<Category>,
    availablePaymentMethods: List<PaymentMethod>,
    initialStartDate: LocalDate,
    initialEndDate: LocalDate
) {
    val selectedCategories = remember { mutableStateListOf<Category>() }
    val selectedPaymentMethods = remember { mutableStateListOf<PaymentMethod>() }
    var startDate by remember { mutableStateOf(initialStartDate) }
    var endDate by remember { mutableStateOf(initialEndDate) }

    var datePickerTarget by remember { mutableStateOf(DatePickerTarget.NONE) }

    if (datePickerTarget != DatePickerTarget.NONE) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { datePickerTarget = DatePickerTarget.NONE },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate =
                                Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            if (datePickerTarget == DatePickerTarget.START) {
                                startDate = newDate
                            } else {
                                endDate = newDate
                            }
                        }
                        datePickerTarget = DatePickerTarget.NONE
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    datePickerTarget = DatePickerTarget.NONE
                }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Filtro Avançado", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Categorias", style = MaterialTheme.typography.titleSmall)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 120.dp)
                ) {
                    items(availableCategories) { category ->
                        SelectableRow(
                            text = category.name,
                            isSelected = selectedCategories.contains(category),
                            onToggle = {
                                if (selectedCategories.contains(category)) {
                                    selectedCategories.remove(category)
                                } else {
                                    selectedCategories.add(category)
                                }
                            }
                        )
                    }
                }

                Text("Formas de Pagamento", style = MaterialTheme.typography.titleSmall)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 120.dp) // Reduzido para dar espaço
                ) {
                    items(availablePaymentMethods) { paymentMethod ->
                        SelectableRow(
                            text = paymentMethod.displayName,
                            isSelected = selectedPaymentMethods.contains(paymentMethod),
                            onToggle = {
                                if (selectedPaymentMethods.contains(paymentMethod)) {
                                    selectedPaymentMethods.remove(paymentMethod)
                                } else {
                                    selectedPaymentMethods.add(paymentMethod)
                                }
                            }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        PickerField(
                            label = "De",
                            value = DateTimeFormatter.ofPattern("dd/MM/yy").format(startDate),
                            onClick = { datePickerTarget = DatePickerTarget.START },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Data de Início"
                                )
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PickerField(
                            label = "Até",
                            value = DateTimeFormatter.ofPattern("dd/MM/yy").format(endDate),
                            onClick = { datePickerTarget = DatePickerTarget.END },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Data de Fim"
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onApplyClick(
                    AdvancedFilterState(
                        selectedCategories = selectedCategories.toList(),
                        selectedPaymentMethod = selectedPaymentMethods.toList(),
                        startDate = startDate,
                        endDate = endDate
                    )
                )
            }) { Text("Aplicar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
private fun SelectableRow(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}