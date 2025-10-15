package com.example.controlegasto.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlegasto.presentation.add_expense.AddExpenseSheet
import com.example.controlegasto.presentation.components.cards.ExpenseCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import com.example.controlegasto.presentation.components.ChartLegend
import com.example.controlegasto.presentation.components.PieChart
import com.example.controlegasto.presentation.theme.ButtonColor
import com.example.controlegasto.presentation.theme.ControleGastoTheme
import com.example.controlegasto.presentation.theme.LightBlue2
import com.example.controlegasto.presentation.theme.TopBar
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory)
) {

    val uiState by viewModel.uiState.collectAsState()
    val expensesWithCategory by viewModel.expensesWithCategory.collectAsState()
    val totalAmountForToday by viewModel.totalAmountForToday.collectAsState()
    val pieChartDataForToday by viewModel.pieChartDataForToday.collectAsState()


    if (uiState.isAddExpanseDialogVisible) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onDialogDismiss,
        ) {
            AddExpenseSheet(
                onDismissRequest = viewModel::onDialogDismiss,
                onSaveClick = { expense ->
                    viewModel.onSaveExpanse(expense)
                    viewModel.onDialogDismiss()
                },
                expenseToEdit = uiState.expenseToEdit?.expense,
                categoryToEdit = uiState.expenseToEdit?.category
            )
        }
    }

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
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text("Meu Bolso") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onAddExpanseClicked,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
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
                            data = pieChartDataForToday,
                            modifier = Modifier.fillMaxSize().padding(start = 10.dp)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total")
                            Text(
                                text = totalAmountForToday,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    ChartLegend(
                        data = pieChartDataForToday,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Text(
                    text = "Despesas de Hoje",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = expensesWithCategory,
                        key = { _, item -> item.expense.id }
                    ) {_,  item ->
                        ExpenseCard(
                            item = item,
                            onEditClick = { viewModel.onEditExpense(item) },
                            onDeleteClick = { viewModel.requestDeleteConfirmation(item.expense) },
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(
                                    durationMillis = 600
                                )
                        ))
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    )
}

@Composable
fun DeleteExpenseConfirmationDialog(
    expenseValue: BigDecimal,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Exclusão") },
        text = {
            Text("Tem a certeze de que deseja apagar o gasto com o valor \"$expenseValue\"? Esta ação não pode ser desfeita")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Apagar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ControleGastoTheme {
        HomeScreen()
    }
}