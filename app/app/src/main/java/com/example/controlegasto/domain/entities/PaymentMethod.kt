package com.example.controlegasto.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PaymentMethod(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
)
