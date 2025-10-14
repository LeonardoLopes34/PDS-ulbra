package com.example.controlegasto.domain.entities

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import com.example.controlegasto.R

enum class PaymentMethod(val displayName: String, @DrawableRes val iconResId: Int) {

    PIX("Pix", R.drawable.pix_svgrepo_com),
    CREDIT_CARD("Cartão de Crédito", R.drawable.baseline_credit_card_24),
    DEBIT_CARD("Cartão de Débito", R.drawable.baseline_credit_card_24),
    MONEY("Dinheiro", R.drawable.outline_money_24);
}
