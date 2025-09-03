package com.example.controlegasto.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "expenses")
data class Expense (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: BigDecimal,
    val categoryId: Int?,
    val description: String,
    val paymentMethod: PaymentMethod?,
    val date: LocalDate
)