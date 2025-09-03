package com.example.controlegasto.data.repository

import com.example.controlegasto.data.dao.ExpenseDao
import com.example.controlegasto.domain.entities.Expense
import kotlinx.coroutines.flow.Flow

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

    override fun getExpenseByCategory(categoryId: Int): Flow<List<Expense>> = expenseDao.getExpenseByCategory(categoryId)

    //methods for get expense by date, category
}