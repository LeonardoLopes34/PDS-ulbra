package com.example.controlegasto.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlegasto.presentation.cards.ExpenseCard
import com.example.controlegasto.presentation.theme.ButtonColor
import com.example.controlegasto.presentation.theme.ControleGastoTheme
import com.example.controlegasto.presentation.theme.LightBlue
import com.example.controlegasto.presentation.theme.LightBlue2
import com.example.controlegasto.presentation.theme.TextColorTotal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = viewModel(factory = ReportViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val expenseWithCategory by viewModel.expenseWithCategory.collectAsState()



    Scaffold(
        containerColor = LightBlue2,
        modifier = Modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBlue,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = { Text("Relatórios") },
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
        floatingActionButton = {
            FloatingActionButton(
                containerColor = ButtonColor,
                onClick = {/* TODO add expense button*/ },
                modifier = Modifier.size(80.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Gasto"
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    "Total do dia: R$00000000",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextColorTotal
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
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(DateFilterType.entries) { filterType ->
                        FilterChip(
                            selected = (uiState.activeFilter == filterType),
                            onClick = { viewModel.onFilterSelected(filterType) },
                            label = { Text(filterType.displayName) },
                            leadingIcon = if (uiState.activeFilter == filterType) {
                                { Icon(Icons.Filled.Done, "Selecionado") }
                            } else {
                                null
                            }
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(expenseWithCategory) {index, item ->
                        ExpenseCard(
                            item = item,
                            expenseNumeration = index + 1,
                            onEditClick = { /* TODO: viewModel.onEditExpense(item.expense) */ },
                            onDeleteClick = { /* TODO: viewModel.onDeleteExpense(item.expense) */ },
                            modifier = Modifier
                        )
                    }
                }

            }
        }
    )
}

