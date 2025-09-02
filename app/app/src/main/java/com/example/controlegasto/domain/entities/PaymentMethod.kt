package com.example.controlegasto.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PaymentMethod(val displayName: String) {
    PIX("Pix"),
    CREDIT_CARD("Cartão de Crétido"),
    DEBIT_CARD("Cartão de Débito"),
    MONEY("Dinheiro")
}
