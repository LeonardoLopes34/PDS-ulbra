package com.example.controlegasto.data.repository

import com.example.controlegasto.presentation.reports.ExpenseWithCategory

interface AIAnalyticsRepository {
    suspend fun getFIlteredExpenseIds(prompt: String, expenses: List<ExpenseWithCategory>): List<Int>
}