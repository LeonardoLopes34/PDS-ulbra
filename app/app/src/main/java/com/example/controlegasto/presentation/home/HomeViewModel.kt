package com.example.controlegasto.presentation.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.data.repository.ExpenseRepository
import com.example.controlegasto.domain.entities.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isAddExpanseDialogVisible: Boolean = false
    // other states of homescreen go here,
)

class HomeViewModel(private val expenseRepository: ExpenseRepository, private val categoryRepository: CategoryRepository): ViewModel() {


    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onAddExpanseClicked() {
        _uiState.update { it.copy(isAddExpanseDialogVisible = true) }
    }
    fun onDialogDismiss() {
        _uiState.update { it.copy(isAddExpanseDialogVisible = false) }
    }

    fun onSaveExpanse(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.addExpense(expense)
        }
    }

    val expensesList: StateFlow<List<Expense>> = expenseRepository.getAllExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

}

//viewmodel factory
object HomeViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

        val expenseRepository = (application as ExpenseControlApplication).expenseRepository
        val categoryRepository = application.categoryRepository
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(expenseRepository, categoryRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}