package com.example.controlegasto.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.controlegasto.presentation.configuration.ConfigurationScreen
import com.example.controlegasto.presentation.home.HomeScreen
import com.example.controlegasto.presentation.reports.ReportScreen

@Composable
fun MainApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomAppBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val navItems = listOf(
                    "Inicio" to Icons.Default.Home,
                    "Relatórios" to Icons.Default.Search,
                    "Configurações" to Icons.Default.Settings
                )
                val screens = listOf(
                    Screen.Home,
                    Screen.Report,
                    Screen.Configuration
                )
                navItems.forEachIndexed { index, item ->
                    val screen = screens[index]
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any {it.route == screen.route} == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = item.second, contentDescription = item.first) },
                        label = { Text(text = item.first) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Report.route) { ReportScreen() }
            composable(Screen.Configuration.route) { ConfigurationScreen() }
        }
    }
}