package com.example.controlegasto.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.data.repository.ExpenseRepository
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.domain.entities.Expense
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.ZoneId

data class ReportUiState(
    val startDate: LocalDate = LocalDate.now().minusDays(7),
    val endDate: LocalDate = LocalDate.now(),
    val activeFilter: DateFilterType = DateFilterType.LAST_7_DAYS,
    val selectedCategories: List<Category> = emptyList(),
    val isAdvancedFilterDialogVisible: Boolean = false
)

data class ExpenseWithCategory(
    val expense: Expense,
    val category: Category
)

class ReportViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState = _uiState.asStateFlow()


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
                newStartDate = today
            }
        }
        _uiState.update {
            it.copy(
                startDate = newStartDate,
                endDate = newEndDate,
                activeFilter = filterType
            )
        }
    }

    fun onApplyAdvancedFilter(filterState: AdvancedFilterState) {
        _uiState.update {
            it.copy(
                selectedCategories = filterState.selectedCategories,
                isAdvancedFilterDialogVisible = false
                ) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredExpenses: StateFlow<List<ExpenseWithCategory>> = uiState.flatMapLatest { state ->
        val startMillis = state.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val endMillis = state.endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()

        val expensesFlow = if(state.selectedCategories.isEmpty()) {
            expenseRepository.getExpensesBetweenDates(startMillis,endMillis)
        } else {
            val categoryIds = state.selectedCategories.map {it.id}
            expenseRepository.getExpensesByCategoriesAndDate(startMillis, endMillis, categoryIds)
        }

            expensesFlow.combine(categoryRepository.getAllCategories()) { expenses, categories ->
                expenses.map { expense ->
                    val category =
                        categories.find { it.id == expense.categoryId } ?: Category.default()
                    ExpenseWithCategory(expense, category)
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


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
}