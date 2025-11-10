package com.example.controlegasto.presentation.configuration

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.controlegasto.ExpenseControlApplication
import com.example.controlegasto.data.network.SyncExpensePayload
import com.example.controlegasto.data.repository.AIAnalyticsRepository
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.data.repository.ExpenseRepository
import com.example.controlegasto.domain.entities.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

data class ConfigurationUiState(
    val isAddCategoryDialogVisible: Boolean = false,
    val categoryForDeletion: Category? = null,
    val categoryToEdit: Category? = null,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null
)

class ConfigurationViewModel(
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository,
    private val aiAnalyticsRepository: AIAnalyticsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConfigurationUiState())
    val uiState = _uiState.asStateFlow()

    fun onAddCategoryClicked() {
        _uiState.update { it.copy(isAddCategoryDialogVisible = true, categoryToEdit = null) }
    }

    fun onAddCategoryDismiss() {
        _uiState.update { it.copy(isAddCategoryDialogVisible = false, categoryToEdit = null) }
    }

    fun onEditCategory(category: Category) {
        _uiState.update { it.copy(isAddCategoryDialogVisible = true, categoryToEdit = category) }
    }

    fun onSaveCategory(name: String, color: Color) {
        viewModelScope.launch {
            val categoryToEdit = _uiState.value.categoryToEdit

            if (categoryToEdit != null) {
                val updatedCategory = categoryToEdit.copy(
                    name = name,
                    color = color.value.toLong()
                )
                categoryRepository.updateCategory(updatedCategory)
            } else {
                val newCategory = Category(
                    name = name,
                    color = color.value.toLong()
                )
                categoryRepository.addCategory(newCategory)
            }
        }
    }


    fun requestDeleteConfirmation(category: Category) {
        _uiState.update { it.copy(categoryForDeletion = category) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(categoryForDeletion = null) }
    }

    fun confirmDelete() {
        val categoryToDelete = _uiState.value.categoryForDeletion
        if (categoryToDelete != null) {
            viewModelScope.launch {
                categoryRepository.deleteCategory(categoryToDelete)
                _uiState.update { it.copy(categoryForDeletion = null) }
            }
        }
    }

    val categoryList: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSyncAllDataClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncMessage = "A sincronizar...")}
            try {
                val allExpenses = expenseRepository.getAllExpenses().first()
                val allCategories = categoryRepository.getAllCategories().first()

                val payloadList = allExpenses.map { expense ->
                    val category = allCategories.find { it.id == expense.categoryId } ?: Category.default()
                    SyncExpensePayload(
                        id = expense.id,
                        value = expense.value.toFloat(),
                        description = expense.description,
                        categoryName = category.name,
                        paymentMethod = expense.paymentMethod?.displayName ?: "N/D",
                        date = expense.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    )
                }
                aiAnalyticsRepository.syncAllExpenses(payloadList)
                _uiState.update { it.copy(isSyncing = false, syncMessage = "Sincronização concluída com sucesso!") }
            } catch (e: Exception) {
                _uiState.update {it.copy(isSyncing = false, syncMessage = "Erro na sincronização? ${e.message}") }
            }
        }
    }
}


object ConfigurationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        val categoryRepository = (application as ExpenseControlApplication).categoryRepository
        val expenseRepository = application.expenseRepository
        val aiAnalyticsRepository = application.aiAnalyticsRepository
        if (modelClass.isAssignableFrom(ConfigurationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfigurationViewModel(categoryRepository, expenseRepository, aiAnalyticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}