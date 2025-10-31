package com.example.controlegasto.presentation.aiAnalysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.data.repository.ExpenseRepository
import com.example.controlegasto.presentation.reports.ExpenseWithCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiAnalysisUiState(
    val prompt: String = "",
    val isLoading: Boolean = false,
    val aiResponseText: String? = null,
    val filteredExpenses: List<ExpenseWithCategory> = emptyList()
)

class AiAnalysisViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AiAnalysisUiState())
    val uiState = _uiState.asStateFlow()

    fun onPromptChange(newPrompt: String) {
        _uiState.update { it.copy(prompt = newPrompt) }
    }

    fun submitPrompt() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val allExpenses = expenseRepository.getAllExpenses().first()

            //TODO all the ai logic here
            //TODO return the result to the ui
        }
    }
}



object AiAnalysisViewModelFactory: ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        val expenseRepository = (application as ExpenseControlApplication).expenseRepository
        val categoryRepository = application.categoryRepository

        if(modelClass.isAssignableFrom(AiAnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AiAnalysisViewModel(expenseRepository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}