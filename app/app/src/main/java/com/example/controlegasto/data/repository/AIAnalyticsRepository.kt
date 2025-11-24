package com.example.controlegasto.data.repository

import com.example.controlegasto.data.network.AIFilterResponse
import com.example.controlegasto.data.network.ReceiptAnalysisResponse
import com.example.controlegasto.data.network.SyncExpensePayload
import java.io.File

interface AIAnalyticsRepository {
    suspend fun getFilteredExpenseIds(prompt: String): AIFilterResponse
    suspend fun syncAllExpenses(expenses: List<SyncExpensePayload>)
    suspend fun processReceiptImage(imageFile: File): ReceiptAnalysisResponse
    suspend fun getFinancialInsights(): String
}