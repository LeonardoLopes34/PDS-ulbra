package com.example.controlegasto

import android.app.Application
import com.example.controlegasto.data.AppDatabase
import com.example.controlegasto.data.network.RetrofitInstance
import com.example.controlegasto.data.repository.AIAnalyticsRepository
import com.example.controlegasto.data.repository.AIAnalyticsRepositoryImpl
import com.example.controlegasto.data.repository.CategoryRepository
import com.example.controlegasto.data.repository.CategoryRepositoryImpl
import com.example.controlegasto.data.repository.ExpenseRepository
import com.example.controlegasto.data.repository.ExpenseRepositoryImpl

class ExpenseControlApplication : Application(){
    private val database by lazy { AppDatabase.getDatabase(this) }

    val categoryRepository: CategoryRepository by lazy { CategoryRepositoryImpl(database.categoryDao()) }
    val expenseRepository: ExpenseRepository by lazy { ExpenseRepositoryImpl(database.expenseDao()) }

    val aiAnalyticsRepository: AIAnalyticsRepository by lazy {
        AIAnalyticsRepositoryImpl(RetrofitInstance.api)
    }
}