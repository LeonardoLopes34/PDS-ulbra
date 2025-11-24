package com.example.controlegasto.presentation.aiAnalysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.repository.AIAnalyticsRepository
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.data.repository.ExpenseRepository
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.presentation.reports.ExpenseWithCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.exp

data class AiAnalysisUiState(
    val prompt: String = "",
    val isLoading: Boolean = false,
    val aiResponseText: String? = null,
    val filteredExpenses: List<ExpenseWithCategory> = emptyList(),
    val financialInsight: String? = null,
    val isGeneratingInsight: Boolean = false
)

class AiAnalysisViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val aiAnalyticsRepository: AIAnalyticsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AiAnalysisUiState())
    val uiState = _uiState.asStateFlow()

    fun onPromptChange(newPrompt: String) {
        _uiState.update { it.copy(prompt = newPrompt) }
    }

    fun submitPrompt() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val allExpenses = expenseRepository.getAllExpenses().first()
                val allCategories = categoryRepository.getAllCategories().first()

                val expenseWithCategory = allExpenses.map {expense ->
                    val category = allCategories.find{it.id == expense.categoryId} ?: Category.default()
                    ExpenseWithCategory(expense, category)
                }

                val response = aiAnalyticsRepository.getFilteredExpenseIds(
                    prompt = _uiState.value.prompt
                )

                val filteredIds = response.filteredExpenseIds ?: emptyList()

                val filteredExpensesWithCategory: List<ExpenseWithCategory>

                if (filteredIds.isNotEmpty()) {
                    val allCategories = categoryRepository.getAllCategories().first()
                    val expenses = filteredIds.mapNotNull { id ->
                        expenseRepository.getExpenseById(id)
                    }
                    filteredExpensesWithCategory = expenses.map { expense ->
                        val category = allCategories.find { it.id == expense.categoryId } ?: Category.default()
                        ExpenseWithCategory(expense, category)
                    }
                } else {
                    filteredExpensesWithCategory = emptyList()
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        aiResponseText = response.analysis ?: "Resposta recebida.",
                        filteredExpenses = filteredExpensesWithCategory
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, aiResponseText = "Erro: ${e.message}") }
            }
        }
    }

    fun generateInsight() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingInsight = true) }
            try {
                val insight = aiAnalyticsRepository.getFinancialInsights()
                _uiState.update {
                    it.copy(
                        isGeneratingInsight = false,
                        financialInsight = insight
                    )
                }
            } catch (e: Error) {
                _uiState.update {
                    it.copy(
                        isGeneratingInsight = false,
                        financialInsight = "Não foi possivel gerar insights"
                    )
                }
            }
        }
    }

}



object AiAnalysisViewModelFactory: ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        val expenseRepository = (application as ExpenseControlApplication).expenseRepository
        val categoryRepository = application.categoryRepository
        val aiAnalyticsRepository = application.aiAnalyticsRepository

        if(modelClass.isAssignableFrom(AiAnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AiAnalysisViewModel(expenseRepository, categoryRepository, aiAnalyticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}