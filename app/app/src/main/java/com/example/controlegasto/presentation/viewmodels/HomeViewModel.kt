package com.example.controlegasto.presentation.viewmodels
import androidx.lifecycle.ViewModel
import com.example.controlegasto.domain.entities.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val isAddExpanseDialogVisible: Boolean = false
    // other states of homescreen go here,
)

class HomeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onAddExpanseClicked() {
        _uiState.update { it.copy(isAddExpanseDialogVisible = true) }
    }
    fun onDialogDismiss() {
        _uiState.update { it.copy(isAddExpanseDialogVisible = false) }
    }

    fun onSaveExpanse(expense: Expense) {
        //TODO: save the expanse with repository for persistance
    }
}