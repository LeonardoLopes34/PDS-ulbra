package com.example.controlegasto.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.domain.entities.PaymentMethod
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ExpenseDao {

    @Insert
    suspend fun createExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Int): Expense?

    @Query("SELECT * FROM expenses ORDER BY value")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getExpenseByCategory(categoryId: Int): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate AND categoryId IN (:categoryIds) ORDER BY date DESC")
    fun getExpensesByCategoryAndDate(startDate: Long, endDate: Long, categoryIds: List<Int>): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate AND paymentMethod IN (:paymentMethods) ORDER BY date DESC")
    fun getExpensesByPaymentMethodsAndDate(startDate: Long, endDate: Long, paymentMethods: List<PaymentMethod>): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate AND categoryId IN (:categoryIds) AND paymentMethod IN (:paymentMethods) ORDER BY date DESC")
    fun getExpensesByAllFilters(startDate: Long, endDate: Long, categoryIds: List<Int>, paymentMethods: List<PaymentMethod>): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id DESC ")
    fun getExpensesForDate(date:Long): Flow<List<Expense>>
}