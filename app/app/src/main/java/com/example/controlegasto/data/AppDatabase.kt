package com.example.controlegasto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.controlegasto.data.dao.CategoryDao
import com.example.controlegasto.data.dao.ExpenseDao
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.domain.entities.Expense

@Database(entities = [Expense::class, Category::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "controle_gasto_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}