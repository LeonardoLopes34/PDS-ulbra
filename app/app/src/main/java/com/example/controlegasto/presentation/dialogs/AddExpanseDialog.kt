package com.example.controlegasto.presentation.dialogs

import android.widget.Toast
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.controlegasto.R
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.domain.entities.PaymentMethod
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    onDismissRequest: () -> Unit,
    onSaveClick: (Expense) -> Unit
) {
    var expense by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var category by remember { mutableStateOf<Category?>(null) }
    var description by remember { mutableStateOf("") }

    // state for bottom sheet menu
    var showCategorySheet by remember { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState()
    val categoryList = listOf<Category>()
    var showPaymentMethodSheet by remember { mutableStateOf(false) }
    val paymentMethodSheetState = rememberModalBottomSheetState()
    val paymentMethodList = listOf<PaymentMethod>()

    // calendar control
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    // calendar for picked date
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate =
                                Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                        }
                    }
                ) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // bottomsheet for category
    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = {showCategorySheet = false},
            sheetState = categorySheetState
        ) {
            LazyColumn {
                items (categoryList) { categoryItem ->
                    ListItem(
                        headlineContent = {Text(categoryItem.name)},
                        modifier = Modifier.clickable{
                            category = categoryItem
                            showCategorySheet = false
                        }
                    )
                }
            }
        }
    }

    // bottomsheet for payment method
    if (showPaymentMethodSheet) {
        ModalBottomSheet(
            onDismissRequest = {showPaymentMethodSheet = false},
            sheetState = paymentMethodSheetState
        ) {
            LazyColumn {
                items(paymentMethodList) {paymentMethodItem ->
                    ListItem(
                        headlineContent = {Text(paymentMethodItem.name)},
                        modifier = Modifier.clickable{
                            paymentMethod = paymentMethodItem
                            showPaymentMethodSheet = false
                        }
                    )
                }
            }
        }
    }


    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
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
                    value = expense,
                    onValueChange = { expense = it },
                    label = { Text("Valor") },
                    prefix = { Text("R$:") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                PickerField(
                    label = "Forma de pagemento",
                    value = paymentMethod?.name ?: "Selecione uma forma de pagemento",
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_wallet_24),
                            contentDescription = "Icone de carteira"
                        )
                    },
                    onClick = {/*TODO */ } // dropdownmenu for categorys
                )
                PickerField(
                    label = "Categoria",
                    value = category?.name ?: "Selecione uma categoria",
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_shopping_cart_24),
                            contentDescription = "Icone da categoria"
                        )
                    },
                    onClick = {/* TODO */ }
                )
                val formattedDateText = remember(selectedDate) {
                    DateTimeFormatter.ofPattern("dd/MM/yyyy").format(selectedDate)
                }
                PickerField(
                    label = "Data",
                    value = formattedDateText.ifEmpty { "Selecione uma data" },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_calendar_today_24),
                            contentDescription = "Icone de calendario"
                        )
                    },
                    onClick = { showDatePicker = true }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    placeholder = { Text("Breve descriçao do motivo da compra:") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                )
                //error if the fields are empty
                if(errorMessage != null) {
                    Text(
                        text = errorMessage!!,
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
                    Button(
                        onClick = {
                            val valueConverted = expense.replace(",", ".").toBigDecimalOrNull()
                            val currentCategory = category
                            val currentPaymentMethod = paymentMethod
                            if(valueConverted != null && category != null && paymentMethod != null) {
                                val newExpanse = Expense(
                                    value = valueConverted,
                                    description = description,
                                    category = currentCategory,
                                    paymentMethod = currentPaymentMethod,
                                    date = selectedDate

                                )
                                onSaveClick(newExpanse)
                            } else {
                                errorMessage = "Preencha todos os campos"
                            }

                        }) {
                        Text("Salvar")
                    }
                }
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
    // Fundo escuro para o preview se destacar
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        AddExpenseDialog(onDismissRequest = {}, onSaveClick = {})
    }
}