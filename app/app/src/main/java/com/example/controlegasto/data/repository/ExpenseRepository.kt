package com.example.controlegasto.data.repository

import com.example.controlegasto.domain.entities.Expense
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun getExpenseById(expenseId: Int): Expense?
    suspend fun addExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    fun getExpenseByCategory(categoryId: Int): Flow<List<Expense>>
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Expense>>
    fun getExpensesByCategoriesAndDate(startDate: Long, endDate: Long, categoryIds: List<Int>): Flow<List<Expense>>
}