package com.example.controlegasto.presentation.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.data.repository.ExpenseRepository
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.presentation.reports.ExpenseWithCategory
import com.example.controlegasto.presentation.reports.PieChartData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import kotlin.math.exp

data class HomeUiState(
    val isAddExpanseDialogVisible: Boolean = false,
    val expenseToEdit: Expense? = null,
    val expenseForDeletion: Expense? = null
)

class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onAddExpanseClicked() {
        _uiState.update { it.copy(isAddExpanseDialogVisible = true, expenseToEdit = null) }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(isAddExpanseDialogVisible = false, expenseToEdit = null) }
    }

    fun onEditExpense(expense: Expense) {
        _uiState.update { it.copy(isAddExpanseDialogVisible = true, expenseToEdit = expense) }
    }

    fun onSaveExpanse(expense: Expense) {
        viewModelScope.launch {
            val expenseToEdit = _uiState.value.expenseToEdit
            if (expenseToEdit != null) {
                val updatedExpense = expenseToEdit.copy(
                    value = expense.value,
                    description = expense.description,
                    categoryId = expense.categoryId,
                    paymentMethod = expense.paymentMethod,
                    date = expense.date
                )
                expenseRepository.updateExpense(updatedExpense)
            } else {
                expenseRepository.addExpense(expense)
            }

        }
    }

    fun requestDeleteConfirmation(expense: Expense) {
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
                _uiState.update { it.copy(expenseForDeletion = null) }
            }
        }
    }


    val expensesWithCategory: StateFlow<List<ExpenseWithCategory>> =
        expenseRepository.getExpenseForDate(LocalDate.now())
            .combine(categoryRepository.getAllCategories()) { allExpenses, allCategories ->
                allExpenses.map { expense ->
                    val category =
                        allCategories.find { it.id == expense.categoryId } ?: Category.default()
                    ExpenseWithCategory(expense, category)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAmountForToday: StateFlow<String> = expensesWithCategory.map { expenses ->
        val total = expenses.sumOf{ it.expense.value.toDouble() }
        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(total)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "R$ 0,00")


    val pieChartDataForToday: StateFlow<List<PieChartData>> = expensesWithCategory.map { expenses ->
        val overallTotal = expenses.sumOf { it.expense.value.toDouble() }.toFloat()
        if (overallTotal == 0f) return@map emptyList()

        expenses.groupBy { it.category }.map { (category, expenseList) ->
            val categoryTotal = expenseList.sumOf { it.expense.value.toDouble() }.toFloat()
            val percentage = (categoryTotal / overallTotal) * 100f
            PieChartData(
                categoryName = category.name,
                totalValue = categoryTotal,
                color = Color(category.color.toULong()),
                percentage = percentage
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}


//viewmodel factory
object HomeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

        val expenseRepository = (application as ExpenseControlApplication).expenseRepository
        val categoryRepository = application.categoryRepository
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(expenseRepository, categoryRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}