package com.example.controlegasto.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.impl.WorkManagerImpl
import androidx.work.setInputMerger
import androidx.work.workDataOf
import com.example.controlegasto.data.dao.ExpenseDao
import com.example.controlegasto.data.worker.SyncExpenseWorker
import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.domain.entities.PaymentMethod
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.exp

class ExpenseRepositoryImpl(private val expenseDao: ExpenseDao, private val context: Context) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    override suspend fun getExpenseById(expenseId: Int): Expense? {
        return expenseDao.getExpenseById(expenseId)
    }

    override suspend fun addExpense(expense: Expense) {
        val expense = expenseDao.createExpense(expense)
        scheduleExpenseSync(expense.toInt())
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
        scheduleExpenseSync(expense.id)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    override fun getExpenseByCategory(categoryId: Int): Flow<List<Expense>> =
        expenseDao.getExpenseByCategory(categoryId)


    override fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
    }

    override fun getExpensesByCategoriesAndDate(
        startDate: Long,
        endDate: Long,
        categoryIds: List<Int>
    ): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategoryAndDate(startDate, endDate, categoryIds)
    }

    override fun getExpensesByPaymentMethodsAndDate(
        startDate: Long,
        endDate: Long,
        paymentMethods: List<PaymentMethod>
    ): Flow<List<Expense>> {
        return expenseDao.getExpensesByPaymentMethodsAndDate(startDate, endDate, paymentMethods)
    }

    override fun getExpensesByAllFilters(
        startDate: Long,
        endDate: Long,
        categoryIds: List<Int>,
        paymentMethods: List<PaymentMethod>
    ): Flow<List<Expense>> {
        return expenseDao.getExpensesByAllFilters(startDate, endDate, categoryIds, paymentMethods)
    }

    override fun getExpenseForDate(date: LocalDate): Flow<List<Expense>> {
        val dateInMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return expenseDao.getExpensesForDate(dateInMillis)
    }


    private fun scheduleExpenseSync(expenseId: Int) {
        val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = workDataOf(SyncExpenseWorker.KEY_EXPENSE_ID to expenseId)

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncExpenseWorker>()
            .setConstraints(constrains)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }


}