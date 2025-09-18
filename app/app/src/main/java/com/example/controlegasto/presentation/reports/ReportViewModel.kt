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
import kotlin.math.exp

data class ReportUiState(
    val startDate: LocalDate = LocalDate.now().minusDays(7),
    val endDate: LocalDate = LocalDate.now(),
    val activeFilter: DateFilterType = DateFilterType.LAST_7_DAYA
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

            DateFilterType.LAST_7_DAYA -> {
                newStartDate = today.minusDays(6)
            }

            DateFilterType.THIS_MONTH -> {
                newStartDate = today.withDayOfMonth(1)
            }
        }
        _uiState.update {
            it.copy(
                startDate = newStartDate,
                endDate = newEndDate,
                activeFilter = filterType
            )
        }

        val filteredExpenses: StateFlow<List<Expense>> = uiState.flatMapLatest { state ->
            expenseRepository.getExpensesBetweenDates(state.startDate, state.endDate)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), emptyList())
    }

    val expenseWithCategory: StateFlow<List<ExpenseWithCategory>> =
        expenseRepository.getAllExpenses()
            .combine(categoryRepository.getAllCategories()) {allExpenses, allCategories ->
                allExpenses.map { expense ->
                    val category = allCategories.find{it.id == expense.categoryId } ?: Category.default()
                    ExpenseWithCategory(expense, category)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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