package com.example.controlegasto.data.network

import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.domain.entities.PaymentMethod
import com.example.controlegasto.presentation.reports.ExpenseWithCategory
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class AIFilterRequest(
    val prompt: String
)

data class AIFilterResponse(
    val filteredExpenseIds: List<Int>?,
    val analysis: String?
)

data class SyncExpensePayload(
    val id: Int,
    val value: Float,
    val description: String,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    val date: String
)

data class SyncRequest(
    val expense: SyncExpensePayload
)

data class BulkSyncRequest(
    val expenses: List<SyncExpensePayload>
)

data class ReceiptAnalysisResponse(
    val totalValue: Float,
    val description: String,
    val suggestedCategory: String,
    val paymentMethod: String?,
    val date: String?
)

interface ApiService {
    @POST("filter-expenses")
    suspend fun getFilteredExpenseIds(@Body request: AIFilterRequest): AIFilterResponse


    @POST("sync-expense")
    suspend fun syncExpense(@Body request: SyncRequest)

    @POST("bulk-sync-expenses")
    suspend fun bulkSyncExpenses(@Body request: BulkSyncRequest)

    @Multipart
    @POST("process-receipt")
    suspend fun processReceipt(@Part image: MultipartBody.Part): ReceiptAnalysisResponse
}