package com.example.controlegasto.data.network

import com.example.controlegasto.presentation.reports.ExpenseWithCategory
import retrofit2.http.Body
import retrofit2.http.POST

data class AIFilterRequest(
    val prompt: String,
    val expenses: List<ExpenseWithCategory>
)

data class AIFilterResponse(
    val filteredExpenseIds: List<Int>
)

interface ApiService {
    @POST("filter-expenses")
    suspend fun getFilteredExpenseIds(@Body request: AIFilterRequest): AIFilterResponse
}