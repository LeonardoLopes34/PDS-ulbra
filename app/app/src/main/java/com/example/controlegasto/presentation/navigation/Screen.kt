package com.example.controlegasto.presentation.navigation

sealed class Screen(val route: String) {
    object Home: Screen("home")
    object Report: Screen("report")
    object Configuration: Screen("configuration")
    object AiAnalysis: Screen("ai_analysis")
}