package com.example.controlegasto.presentation.home

import android.app.AlertDialog
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlegasto.presentation.add_expense.AddExpenseSheet
import com.example.controlegasto.presentation.cards.ExpenseCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import com.example.controlegasto.presentation.components.ChartLegend
import com.example.controlegasto.presentation.components.PieChart
import com.example.controlegasto.presentation.theme.ButtonColor
import com.example.controlegasto.presentation.theme.ControleGastoTheme
import com.example.controlegasto.presentation.theme.LightBlue2
import com.example.controlegasto.presentation.theme.TextColorTotal
import com.example.controlegasto.presentation.theme.TopBar
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory)
) {

    val uiState by viewModel.uiState.collectAsState()
    val expensesWithCategory by viewModel.expensesWithCategory.collectAsState()
    val totalAmountForToday by viewModel.totalAmountForToday.collectAsState()
    val pieChartDataForToday by viewModel.pieChartDataForToday.collectAsState()


    if(uiState.isAddExpanseDialogVisible) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onDialogDismiss,
        ) {
            AddExpenseSheet(
                onDismissRequest = viewModel::onDialogDismiss,
                onSaveClick = { expense ->
                    viewModel.onSaveExpanse(expense)
                    viewModel.onDialogDismiss()
                }
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
        containerColor = LightBlue2,
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBar,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = { Text("Meu Bolso") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onAddExpanseClicked,
                containerColor = ButtonColor,
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
                            data = pieChartDataForToday,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total", style = MaterialTheme.typography.bodySmall)
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
                        modifier = Modifier.weight(0.4f)
                    )
                }
                Text(
                    text = "Despesas de Hoje",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                   itemsIndexed(expensesWithCategory) {index, item ->
                       ExpenseCard(
                           item = item,
                           expenseNumeration = index + 1,
                           onEditClick = { viewModel.onEditExpense(item.expense) },
                           onDeleteClick = { viewModel.requestDeleteConfirmation(item.expense) },
                           modifier = Modifier
                       )
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
        title= { Text("Confirmar Exclusão")},
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