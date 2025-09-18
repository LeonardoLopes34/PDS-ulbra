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
    fun getExpensesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>
}