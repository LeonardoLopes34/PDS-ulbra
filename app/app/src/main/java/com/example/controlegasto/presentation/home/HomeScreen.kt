package com.example.controlegasto.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.controlegasto.presentation.theme.ButtonColor
import com.example.controlegasto.presentation.theme.ControleGastoTheme
import com.example.controlegasto.presentation.theme.LightBlue
import com.example.controlegasto.presentation.theme.LightBlue2
import com.example.controlegasto.presentation.theme.TextColorTotal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory)
) {

    val uiState by viewModel.uiState.collectAsState()
    val expenses by viewModel.expensesList.collectAsState()

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

    Scaffold(
        containerColor = LightBlue2,
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBlue,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = { Text("Menu") }
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
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    "Total do dia: R$ 00000", style = MaterialTheme.typography.titleLarge
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(CircleShape) // Define a forma como um círculo
                        .background(MaterialTheme.colorScheme.secondaryContainer), // Aplica a cor de fundo
                    contentAlignment = Alignment.Center
                ) {
                    Text("Teste")
                }
                LazyColumn(   //trocar essa row por uma lazyrow para evitar os cards de ficarem espremidos
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                   itemsIndexed(expenses) {index, expense ->
                       ExpenseCard(
                           expanseNumeration = index + 1,
                           expanseTotal = expense.value,
                           description = expense.description,
                           category = "teste", //get category with categoryid
                           modifier = Modifier
                       )
                   }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Total: R$ 00000000",
                        color = TextColorTotal,
                        fontSize = 30.sp,
                        style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
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