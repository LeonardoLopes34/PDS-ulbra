package com.example.controlegasto.data.repository

import com.example.controlegasto.data.network.AIFilterRequest
import com.example.controlegasto.data.network.ApiService
import com.example.controlegasto.presentation.reports.ExpenseWithCategory

class AIAnalyticsRepositoryImpl(private val apiService: ApiService): AIAnalyticsRepository {
    override suspend fun getFIlteredExpenseIds(prompt: String, expenses: List<ExpenseWithCategory>): List<Int> {
        val request = AIFilterRequest(prompt, expenses)
        val response = apiService.getFilteredExpenseIds(request)
        return response.filteredExpenseIds
    }

}