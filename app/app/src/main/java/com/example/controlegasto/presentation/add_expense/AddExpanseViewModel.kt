package com.example.controlegasto.presentation.add_expense

import androidx.lifecycle.ViewModel
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.domain.entities.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class AddExpanseUiState(
    val expanseValue: String = "",
    val expanseDescription: String = "",
    val expanseSelectedDate: LocalDate = LocalDate.now(),
    val expanseSelectedCategory: Category? = null,
    val expanseSelectedPaymentMethod: PaymentMethod? = null,
    val isDatePickerSelected: Boolean = false,
    val isCategorySheetVisible: Boolean = false,
    val isPaymentMethodSheetVisible: Boolean = false,
    val errorMessage: String? = "",
    val categoryList: List<Category> = emptyList(),
    val paymentMethodList: List<PaymentMethod> = emptyList()
)

class AddExpanseViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AddExpanseUiState())
    val uiState = _uiState.asStateFlow()

    fun onExpanseValueChanged(value: String) {
        _uiState.update { it.copy(expanseValue = value) }
    }

    init {
        _uiState.update {
            it.copy(
                paymentMethodList = PaymentMethod.entries.toList(),
            )
        }
    }

    fun onExpanseDescriptionChanged(description: String) {
        _uiState.update { it.copy(expanseDescription = description) }
    }

    fun onExpanseDateSelected(date: LocalDate) {
        _uiState.update { it.copy(expanseSelectedDate = date) }
    }

    fun onExpanseSelectedCategory(category: Category) {
        _uiState.update { it.copy(expanseSelectedCategory = category, isCategorySheetVisible = false) }
    }

    fun onExpanseSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        _uiState.update { it.copy(expanseSelectedPaymentMethod = paymentMethod, isPaymentMethodSheetVisible = false) }
    }

    fun onDatePickerDismiss() {
        _uiState.update { it.copy(isDatePickerSelected = false) }
    }

    fun onCategoryPickerDismiss() {
        _uiState.update { it.copy(isCategorySheetVisible = false) }
    }

    fun onPaymentMethodPickerDismiss() {
        _uiState.update { it.copy(isPaymentMethodSheetVisible = false) }
    }

    fun onOpenDatePicker() {
        _uiState.update { it.copy(isDatePickerSelected = true) }
    }

    fun onCategoryPicker() {
        _uiState.update { it.copy(isCategorySheetVisible = true) }
    }

    fun onPaymentMethodPicker() {
        _uiState.update { it.copy(isPaymentMethodSheetVisible = true) }
    }

    fun onSaveTapped(onSaveSuccess: (Expense) -> Unit) {
        val numValue = _uiState.value.expanseValue.replace(",", ".").toBigDecimalOrNull()
        val currentCategory = _uiState.value.expanseSelectedCategory
        val currentPaymentMethod = _uiState.value.expanseSelectedPaymentMethod

        if(numValue == null || currentCategory == null || currentPaymentMethod == null) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos obrigatorios") }
            return
        }
        val newExpense = Expense(
            value = numValue,
            description = _uiState.value.expanseDescription,
            categoryId = currentCategory.id,
            paymentMethod = currentPaymentMethod,
            date = _uiState.value.expanseSelectedDate
        )

        _uiState.update { it.copy(errorMessage = null) }
        onSaveSuccess(newExpense)

    }
    // get category and payment methods from repository
}