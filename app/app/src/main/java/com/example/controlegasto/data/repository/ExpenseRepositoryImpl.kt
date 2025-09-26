package com.example.controlegasto.data.repository

import com.example.controlegasto.data.dao.ExpenseDao
import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.domain.entities.PaymentMethod
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import kotlin.math.exp

class ExpenseRepositoryImpl(private val expenseDao: ExpenseDao) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    override suspend fun getExpenseById(expenseId: Int): Expense? {
        return expenseDao.getExpenseById(expenseId)
    }

    override suspend fun addExpense(expense: Expense) {
        expenseDao.createExpense(expense)
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
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


}