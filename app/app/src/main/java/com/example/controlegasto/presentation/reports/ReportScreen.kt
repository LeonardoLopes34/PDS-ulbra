package com.example.controlegasto.presentation.reports

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.controlegasto.presentation.add_expense.AddExpenseSheet
import com.example.controlegasto.presentation.components.cards.ExpenseCard
import com.example.controlegasto.presentation.components.AdvancedFilterDialog
import com.example.controlegasto.presentation.components.AiPromptDialog
import com.example.controlegasto.presentation.components.ChartLegend
import com.example.controlegasto.presentation.components.PieChart
import com.example.controlegasto.presentation.home.DeleteExpenseConfirmationDialog
import com.example.controlegasto.presentation.theme.ButtonColor
import com.example.controlegasto.presentation.theme.LightBlue2
import com.example.controlegasto.presentation.theme.TopBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReportScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = viewModel(factory = ReportViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
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

    // Aiprompt
    if (uiState.isAIPromptDialogVisible) {
        AiPromptDialog(
            onDismissRequest = viewModel::onDismissAIPrompt,
            onApplyClick = viewModel::onApplyAIPrompt
        )
    }

    // add expense visibility
    if (uiState.isAddExpenseDialogVisible) {
        ModalBottomSheet(onDismissRequest = viewModel::onDialogDismiss) {
            AddExpenseSheet(
                onDismissRequest = viewModel::onDialogDismiss,
                onSaveClick = { expense ->
                    uiState.expenseToEdit?.let {
                        viewModel.onUpdateExpense(expense.copy(id = it.expense.id))
                    }
                    viewModel.onDialogDismiss()
                },
                expenseToEdit = uiState.expenseToEdit?.expense,
                categoryToEdit = uiState.expenseToEdit?.category
            )
        }
    }


    // delete expense confirmation
    val expenseToDelete = uiState.expenseForDeletion
    if (expenseToDelete != null) {
        DeleteExpenseConfirmationDialog(
            expenseValue = expenseToDelete.value,
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::cancelDelete
        )
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text("Relatórios") },
                actions = {
                    IconButton(onClick = {viewModel.onExportClicked(context)}) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Exportar Dados"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = viewModel::onOpenAIPrompt,
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
                verticalArrangement = Arrangement.spacedBy(23.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // ring graph
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        PieChart(
                            data = pieChartData,
                            modifier = Modifier.fillMaxSize().padding(start = 10.dp)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total")
                            AnimatedContent(
                                targetState = totalAmountText,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
                                            fadeOut(animationSpec = tween(90)) using
                                            SizeTransform(clip = false)
                                },
                                label = "Animated Total Amount"
                            ) { targetText ->
                                Text(
                                    text = targetText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    ChartLegend(
                        data = pieChartData,
                        modifier = Modifier.padding(start = 16.dp)
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
                                if (filterType == DateFilterType.CUSTOM) {
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
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = expenses,
                        key = { _, item -> item.expense.id }
                    ) { index, item ->
                        ExpenseCard(
                            item = item,
                            onEditClick = { viewModel.onEditExpenseClicked(item) },
                            onDeleteClick = { viewModel.onRequestDeleteConfirmation(item.expense) },
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(
                                    durationMillis = 600
                                )
                            )
                        )
                    }
                }

            }
        }
    )
}

