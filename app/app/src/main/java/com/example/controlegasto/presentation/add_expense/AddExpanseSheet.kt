package com.example.controlegasto.presentation.add_expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlegasto.R
import com.example.controlegasto.domain.entities.Expense
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    onDismissRequest: () -> Unit,
    onSaveClick: (Expense) -> Unit,
    viewModel: AddExpanseViewModel = viewModel(factory = AddExpenseViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()

    // calendar for picked date
    if (uiState.isDatePickerSelected) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.expanseSelectedDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = viewModel::onDatePickerDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis

                        val selectedDate = selectedMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        } ?: uiState.expanseSelectedDate
                        viewModel.onExpanseDateSelected(selectedDate)
                        viewModel.onDatePickerDismiss()
                    }
                ) {
                    Text("Ok")
                }
            },
            dismissButton = { TextButton(onClick = viewModel::onDatePickerDismiss) { Text("Cancelar") } }

        ) {
            DatePicker(state = datePickerState)
        }
    }


    // bottomsheet for category
    if (uiState.isCategorySheetVisible){
        ModalBottomSheet(onDismissRequest = viewModel::onCategoryPickerDismiss) {
            LazyColumn {
                items(uiState.categoryList) {category ->
                    ListItem(
                        headlineContent = {Text(category.name)},
                        modifier = Modifier.clickable{viewModel.onExpanseSelectedCategory(category)}
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    )
                }
            }
        }
    }
    // bottomsheet for payment method
    if (uiState.isPaymentMethodSheetVisible) {
        ModalBottomSheet(onDismissRequest = viewModel::onPaymentMethodPickerDismiss) {
            LazyColumn {
                items(uiState.paymentMethodList) {paymentMethodItem ->
                    ListItem(
                        headlineContent = {Text(paymentMethodItem.displayName)},
                        modifier = Modifier.clickable{viewModel.onExpanseSelectedPaymentMethod(paymentMethodItem)}
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    )
                }
            }
        }
    }


            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Adicionar despesa",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = uiState.expanseValue,
                    onValueChange = viewModel::onExpanseValueChanged,
                    label = { Text("Valor") },
                    prefix = { Text("R$:") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                PickerField(
                    label = "Forma de pagemento",
                    value = uiState.expanseSelectedPaymentMethod?.displayName ?: "Selecione uma forma de pagemento",
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_wallet_24),
                            contentDescription = "Icone de carteira"
                        )
                    },
                    onClick = viewModel::onPaymentMethodPicker
                )
                PickerField(
                    label = "Categoria",
                    value = uiState.expanseSelectedCategory?.name ?: "Selecione uma categoria",
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_shopping_cart_24),
                            contentDescription = "Icone da categoria"
                        )
                    },
                    onClick = viewModel::onCategoryPicker
                )

                val formattedDateText = remember(uiState.expanseSelectedDate) {
                    DateTimeFormatter.ofPattern("dd/MM/yyyy").format(uiState.expanseSelectedDate)
                }
                PickerField(
                    label = "Data",
                    value = formattedDateText,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_calendar_today_24),
                            contentDescription = "Icone de calendario"
                        )
                    },
                    onClick = viewModel::onOpenDatePicker
                )

                OutlinedTextField(
                    value = uiState.expanseDescription,
                    onValueChange = viewModel::onExpanseDescriptionChanged,
                    label = { Text("Descrição") },
                    placeholder = { Text("Breve descriçao do motivo da compra:") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(onClick = {viewModel.onSaveTapped(onSaveClick) }) {
                        Text("Salvar")
                    }
                }
            }
        }



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerField(
    label: String,
    value: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon()
                Text(text = value)
            }
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Selecionar",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun AddExpenseDialogPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        AddExpenseSheet(onDismissRequest = {}, onSaveClick = {})
    }
}