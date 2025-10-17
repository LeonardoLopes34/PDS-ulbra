package com.example.controlegasto.presentation.reports

import android.content.Context
import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.data.repository.ExpenseRepository
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.domain.entities.PaymentMethod
import com.example.controlegasto.presentation.components.AdvancedFilterState
import com.patrykandpatrick.vico.core.extension.sumOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FilePermission
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.exp
import kotlin.math.floor

data class ReportUiState(
    val startDate: LocalDate = LocalDate.now().minusDays(7),
    val endDate: LocalDate = LocalDate.now(),
    val activeFilter: DateFilterType = DateFilterType.LAST_7_DAYS,
    val selectedCategories: List<Category> = emptyList(),
    val selectedPaymentMethods: List<PaymentMethod> = emptyList(),
    val isAdvancedFilterDialogVisible: Boolean = false,
    val isAIPromptDialogVisible: Boolean = false,
    val isAddExpenseDialogVisible: Boolean = false,
    val expenseToEdit: ExpenseWithCategory? = null,
    val expenseForDeletion: Expense? = null
)

data class ExpenseWithCategory(
    val expense: Expense,
    val category: Category
)

data class PieChartData(
    val categoryName: String,
    val totalValue: Float,
    val color: Color,
    val percentage: Float
)

class ReportViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState = _uiState.asStateFlow()

    fun onOpenAdvancedFilter() {
        _uiState.update { it.copy(isAdvancedFilterDialogVisible = true) }
    }

    fun onDismissAdvancedFilter() {
        _uiState.update { it.copy(isAdvancedFilterDialogVisible = false) }
    }

    fun onOpenAIPrompt() {
        _uiState.update { it.copy(isAIPromptDialogVisible = true) }
    }

    fun onDismissAIPrompt() {
        _uiState.update { it.copy(isAIPromptDialogVisible = false) }
    }

    fun onApplyAIPrompt(prompt: String) {
        /*TODO function to call AI Agent */
        onDismissAIPrompt()
    }

    fun onEditExpenseClicked(expense: ExpenseWithCategory) {
        _uiState.update { it.copy(isAddExpenseDialogVisible = true, expenseToEdit = expense) }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(isAddExpenseDialogVisible = false, expenseToEdit = null) }
    }

    fun onRequestDeleteConfirmation(expense: Expense) {
        _uiState.update { it.copy(expenseForDeletion = expense) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(expenseForDeletion = null) }
    }

    fun confirmDelete() {
        val expenseToDelete = _uiState.value.expenseForDeletion
        if (expenseToDelete != null) {
            viewModelScope.launch {
                expenseRepository.deleteExpense(expenseToDelete)
                _uiState.update {
                    it.copy(expenseForDeletion = null)
                }
            }
        }
    }

    fun onUpdateExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.updateExpense(expense)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredExpenses: StateFlow<List<ExpenseWithCategory>> = uiState.flatMapLatest { state ->
        val startMillis =
            state.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val endMillis = state.endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
        val categoryIds = state.selectedCategories.map { it.id }
        val paymentMethods = state.selectedPaymentMethods

        val expensesFlow: Flow<List<Expense>> = when {
            categoryIds.isNotEmpty() && paymentMethods.isNotEmpty() -> {
                expenseRepository.getExpensesByAllFilters(
                    startMillis,
                    endMillis,
                    categoryIds,
                    paymentMethods
                )
            }

            categoryIds.isNotEmpty() -> {
                expenseRepository.getExpensesByCategoriesAndDate(
                    startMillis,
                    endMillis,
                    categoryIds
                )
            }

            paymentMethods.isNotEmpty() -> {
                expenseRepository.getExpensesByPaymentMethodsAndDate(
                    startMillis,
                    endMillis,
                    paymentMethods
                )
            }

            else -> {
                expenseRepository.getExpensesBetweenDates(startMillis, endMillis)
            }
        }

        expensesFlow.combine(categoryRepository.getAllCategories()) { expenses, categories ->
            expenses.map { expense ->
                val category =
                    categories.find { it.id == expense.categoryId } ?: Category.default()
                ExpenseWithCategory(expense, category)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentMethods: StateFlow<List<PaymentMethod>> =
        MutableStateFlow(PaymentMethod.entries.toList()).asStateFlow()

    val totalFilteredAmount: StateFlow<String> = filteredExpenses.map { expenseWithCategory ->
        val total = expenseWithCategory.sumOf { it.expense.value.toDouble().toFloat() }
        NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(total)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "R$ 0,00")


    val pieChartData: StateFlow<List<PieChartData>> = filteredExpenses.map { expenseWithCategory ->
        val overallTotal = expenseWithCategory.sumOf { it.expense.value.toDouble().toFloat() }
        if (overallTotal == 0f) return@map emptyList()
        expenseWithCategory
            .groupBy { it.category }
            .map { (category, expenses) ->
                val categoryTotal = expenses.sumOf { it.expense.value.toDouble().toFloat() }
                val percentage = (categoryTotal / overallTotal) * 100f
                PieChartData(
                    categoryName = category.name,
                    totalValue = expenses.sumOf { it.expense.value.toFloat() },
                    color = Color(category.color.toULong()),
                    percentage = percentage
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    fun onFilterSelected(filterType: DateFilterType) {
        val today = LocalDate.now()
        val newStartDate: LocalDate
        val newEndDate: LocalDate = today

        when (filterType) {
            DateFilterType.TODAY -> {
                newStartDate = today
            }

            DateFilterType.LAST_7_DAYS -> {
                newStartDate = today.minusDays(6)
            }

            DateFilterType.THIS_MONTH -> {
                newStartDate = today.withDayOfMonth(1)
            }

            DateFilterType.CUSTOM -> {
                _uiState.update { it.copy(activeFilter = filterType) }
                return
            }
        }
        _uiState.update {
            it.copy(
                startDate = newStartDate,
                endDate = newEndDate,
                activeFilter = filterType,
                selectedCategories = emptyList(),
                selectedPaymentMethods = emptyList()
            )
        }
    }

    fun onApplyAdvancedFilter(filterState: AdvancedFilterState) {
        _uiState.update {
            it.copy(
                selectedCategories = filterState.selectedCategories,
                selectedPaymentMethods = filterState.selectedPaymentMethod,
                startDate = filterState.startDate,
                endDate = filterState.endDate,
                isAdvancedFilterDialogVisible = false
            )
        }
    }

    fun convertExpensesToCsv(expenses: List<ExpenseWithCategory>): String {
        val csvBuilder = StringBuilder()

        csvBuilder.append("Data, Descrição, Categoria, Valor, Forma de Pagamento\n")

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        expenses.forEach { item ->
            val date = item.expense.date.format(formatter)
            val description = item.expense.description.replace(",", ";")
            val category = item.category.name
            val value = item.expense.value.toPlainString()
            val paymentMethod = item.expense.paymentMethod?.displayName

            csvBuilder.append("$date, $description, $category, $value, $paymentMethod")
        }
        return csvBuilder.toString()
    }

    fun onExportClicked(context: Context) {
        viewModelScope.launch {
            val expenseToExport = filteredExpenses.value
            if(expenseToExport.isEmpty()) {
                return@launch
            }

            val csvContent = convertExpensesToCsv(expenseToExport)
            val csvFile = File(context.cacheDir, "despesas.csv")
            csvFile.writeText(csvContent)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                csvFile
            )
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "text/csv"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Exportar despesas para.. "))
        }
    }
}

object ReportViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

        val expenseRepository = (application as ExpenseControlApplication).expenseRepository
        val categoryRepository = application.categoryRepository

        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(expenseRepository, categoryRepository) as T

        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}