package com.example.controlegasto.presentation.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.domain.entities.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConfigurationUiState(
    val isAddCategoryDialogVisible: Boolean = false,
    val categoryForDeletion: Category? = null
)

class ConfigurationViewModel(private val categoryRepository: CategoryRepository): ViewModel() {
    private val _uiState = MutableStateFlow(ConfigurationUiState())
    val uiState = _uiState.asStateFlow()

    fun onAddCategoryClicked() {
        _uiState.update { it.copy(isAddCategoryDialogVisible = true)}
    }
    fun onAddCategoryDismiss() {
        _uiState.update { it.copy(isAddCategoryDialogVisible = false) }
    }

    fun onSaveCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.addCategory(category)
        }
    }

    fun requestDeleteConfirmation(category: Category) {
        _uiState.update { it.copy(categoryForDeletion = category) }
    }

    fun cancelDelete(){
        _uiState.update { it.copy(categoryForDeletion = null) }
    }

    fun confirmDelete() {
        val categoryToDelete = _uiState.value.categoryForDeletion
        if(categoryToDelete != null) {
            viewModelScope.launch {
                categoryRepository.deleteCategory(categoryToDelete)
                _uiState.update { it.copy(categoryForDeletion = null) }
            }
        }
    }

    fun onUpdateCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }

    val categoryList: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}


object ConfigurationViewModelFactory: ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        val categoryRepository = (application as ExpenseControlApplication).categoryRepository
        if (modelClass.isAssignableFrom(ConfigurationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfigurationViewModel(categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}