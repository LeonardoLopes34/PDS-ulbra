package com.example.controlegasto.data.repository

import com.example.controlegasto.data.network.AIFilterRequest
import com.example.controlegasto.data.network.AIFilterResponse
import com.example.controlegasto.data.network.ApiService
import com.example.controlegasto.data.network.BulkSyncRequest
import com.example.controlegasto.data.network.ReceiptAnalysisResponse
import com.example.controlegasto.data.network.SyncExpensePayload
import com.example.controlegasto.presentation.reports.ExpenseWithCategory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AIAnalyticsRepositoryImpl(private val apiService: ApiService): AIAnalyticsRepository {
    override suspend fun getFilteredExpenseIds(prompt: String): AIFilterResponse {
        val request = AIFilterRequest(prompt)
        return apiService.getFilteredExpenseIds(request)
    }

    override suspend fun syncAllExpenses(expenses: List<SyncExpensePayload>) {
        val request = BulkSyncRequest(expenses = expenses)
        apiService.bulkSyncExpenses(request)
    }

    override suspend fun processReceiptImage(imageFile: File): ReceiptAnalysisResponse {
        val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

        return apiService.processReceipt(body)
    }

    override suspend fun getFinancialInsights(): String {
        val response = apiService.generateInsights()
        return response.insightText
    }

}