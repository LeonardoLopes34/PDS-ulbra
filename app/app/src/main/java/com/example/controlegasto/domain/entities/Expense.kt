package com.example.controlegasto.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class Expense (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: BigDecimal,
    val category: Category?,
    val description: String,
    val paymentMethod: PaymentMethod?,
    val date: LocalDate
)