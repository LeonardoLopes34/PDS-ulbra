package com.example.controlegasto.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlegasto.presentation.cards.ExpenseCard
import com.example.controlegasto.presentation.components.AdvancedFilterDialog
import com.example.controlegasto.presentation.components.AiPromptDialog
import com.example.controlegasto.presentation.components.ChartLegend
import com.example.controlegasto.presentation.components.PieChart
import com.example.controlegasto.presentation.theme.ButtonColor
import com.example.controlegasto.presentation.theme.LightBlue2
import com.example.controlegasto.presentation.theme.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = viewModel(factory = ReportViewModel.ReportViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val expenses by viewModel.filteredExpenses.collectAsState()
    val availableCategories by viewModel.categories.collectAsState()
    val availablePaymentMethods by viewModel.paymentMethods.collectAsState()

    val totalAmountText by viewModel.totalFilteredAmount.collectAsState()
    val pieChartData by viewModel.pieChartData.collectAsState()

    if (uiState.isAdvancedFilterDialogVisible) {
        AdvancedFilterDialog(
            onApplyClick = viewModel::onApplyAdvancedFilter,
            onDismissRequest = viewModel::onDismissAdvancedFilter,
            availableCategories = availableCategories,
            availablePaymentMethods = availablePaymentMethods,
            initialStartDate = uiState.startDate,
            initialEndDate = uiState.endDate
        )
    }

    if (uiState.isAIPromptDialogVisible) {
        AiPromptDialog(
            onDismissRequest = viewModel::onDismissAIPrompt,
            onApplyClick = viewModel::onApplyAIPrompt
        )
    }



    Scaffold(
        containerColor = LightBlue2,
        modifier = Modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBar,
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
                onClick = viewModel::onOpenAIPrompt ,
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
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(0.6f),
                        contentAlignment = Alignment.Center
                    ) {
                        PieChart(
                            data = pieChartData,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = totalAmountText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    ChartLegend(
                        data = pieChartData,
                        modifier = Modifier.weight(0.4f)
                    )
                }
                Spacer(modifier = Modifier.height(1.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(DateFilterType.entries) { filterType ->
                        FilterChip(
                            selected = (uiState.activeFilter == filterType),
                            onClick = {
                                if(filterType == DateFilterType.CUSTOM) {
                                    viewModel.onOpenAdvancedFilter()
                                } else {
                                    viewModel.onFilterSelected(filterType)
                                }
                            },
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
                    itemsIndexed(expenses) {index, item ->
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

