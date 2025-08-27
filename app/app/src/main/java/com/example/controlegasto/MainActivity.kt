package com.example.controlegasto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.controlegasto.presentation.home.HomeScreen
import com.example.controlegasto.presentation.theme.ControleGastoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControleGastoTheme {
                HomeScreen()
            }
        }
    }
}
