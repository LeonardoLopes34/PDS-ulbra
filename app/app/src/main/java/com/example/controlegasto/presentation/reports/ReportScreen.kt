package com.example.controlegasto.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.controlegasto.presentation.theme.ButtonColor
import com.example.controlegasto.presentation.theme.ControleGastoTheme
import com.example.controlegasto.presentation.theme.LightBlue
import com.example.controlegasto.presentation.theme.LightBlue2
import com.example.controlegasto.presentation.theme.TextColorTotal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(modifier: Modifier = Modifier) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val navItems = listOf(  //placeholder da bottomBar Navegacao
        "Inicio" to Icons.Default.Home,
        "Relatorios" to Icons.Default.Search,
        "Configurações" to Icons.Default.Settings
    )
    Scaffold (
        containerColor = LightBlue2,
        modifier = Modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBlue,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {Text("Relatórios")},
                navigationIcon = {
                    IconButton(onClick = {/*TODO button to return to home screen*/ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icone de voltar"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = (selectedItemIndex == index),
                        onClick = {selectedItemIndex = index},
                        icon = {
                            Icon(
                                imageVector = item.second,
                                contentDescription = item.first
                            )
                        },
                        label = { Text(text = item.first)}
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = ButtonColor,
                onClick = {/* TODO add expense button*/},
                modifier = Modifier.size(80.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Gasto"
                )
            }
        },
        content = {innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    "Total do dia: R$00000000", style = MaterialTheme.typography.titleLarge, color = TextColorTotal
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Teste Grafico")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReportFilterCard(
                        "Teste",
                        MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    ReportFilterCard(
                        "Teste2",
                        MaterialTheme.colorScheme.scrim,
                        modifier = Modifier.weight(1f)
                    )
                    ReportFilterCard(
                        "Teste3",
                        MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    ReportFilterCard(
                        "Teste2",
                        MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f)
                    )
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {  }
                // usar lazycolumn para fazer a lista de cards com os gastos
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    ControleGastoTheme {
        ReportScreen()
    }
}