package com.example.controlegasto.presentation.add_expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.repository.AIAnalyticsRepository
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.domain.entities.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.isActive
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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
    val paymentMethodList: List<PaymentMethod> = emptyList(),
    val isProcessingReceipt: Boolean = false
)

class AddExpanseViewModel(
    private val categoryRepository: CategoryRepository,
    private val aiAnalyticsRepository: AIAnalyticsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AddExpanseUiState())
    val uiState = _uiState.asStateFlow()

    fun onExpanseValueChanged(value: String) {
        _uiState.update { it.copy(expanseValue = value) }
    }

    init {
        _uiState.update { it.copy(paymentMethodList = PaymentMethod.entries.toList())}
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categoriesFromDb ->
                _uiState.update { it.copy(categoryList = categoriesFromDb) }
            }
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

    fun loadExpense(expense: Expense, category: Category) {
        _uiState.update {
            it.copy(
                expanseValue = expense.value.toPlainString(),
                expanseSelectedCategory = category,
                expanseSelectedPaymentMethod = expense.paymentMethod,
                expanseDescription = expense.description,
                expanseSelectedDate = expense.date
            )
        }
    }
    fun resetState() {
        _uiState.update {
            AddExpanseUiState(
                categoryList = it.categoryList,
                paymentMethodList = it.paymentMethodList
            )
        }
    }
    fun onReceiptImageTaken(imageFile: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingReceipt = true) }
            try {
                val response = aiAnalyticsRepository.processReceiptImage(imageFile)
                val suggestedCategory = withContext(Dispatchers.IO) {
                    categoryRepository.getCategoryByName(response.suggestedCategory)
                } ?: Category.default()

                val suggestedPaymentMethod = PaymentMethod.entries.find {
                    it.name.equals(response.paymentMethod, ignoreCase = true)
                } ?: PaymentMethod.entries.find {
                    it.displayName.equals(response.paymentMethod, ignoreCase = true)
                } ?: PaymentMethod.entries.first()

                _uiState.update {
                    it.copy(
                        isProcessingReceipt = false,
                        expanseValue = response.totalValue.toString(),
                        expanseDescription = response.description,
                        expanseSelectedCategory = suggestedCategory,
                        expanseSelectedPaymentMethod = suggestedPaymentMethod,
                        expanseSelectedDate = if (response.date != null) parseDateSafe(response.date) else LocalDate.now()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessingReceipt = false, errorMessage = "Erro ao ler recibo: ${e.message}") }
            } finally {
                imageFile.delete()
            }
        }
    }
    private fun parseDateSafe(dateString: String?): LocalDate {
        if (dateString.isNullOrBlank()) return LocalDate.now()
        return try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }
}


object AddExpenseViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

        val categoryRepository = (application as ExpenseControlApplication).categoryRepository
        val aiAnalyticsRepository = application.aiAnalyticsRepository

        if (modelClass.isAssignableFrom(AddExpanseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddExpanseViewModel(categoryRepository, aiAnalyticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}