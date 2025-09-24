package com.example.controlegasto.presentation.reports

enum class DateFilterType(val displayName: String) {
    TODAY("Hoje"),
    LAST_7_DAYS("Últimos 7 dias"),
    THIS_MONTH("Esse Mês"),
    CUSTOM("Personalizado")
}