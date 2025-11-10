package com.example.controlegasto.presentation.aiAnalysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.controlegasto.presentation.components.cards.ExpenseCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAnalysisScreen(navController: NavController) {
    val viewModel: AiAnalysisViewModel = viewModel(factory = AiAnalysisViewModelFactory)
    val uiState by viewModel.uiState.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text("Análise com IA") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = uiState.aiResponseText ?: "Faça uma pergunta sobre seus gastos",
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(uiState.filteredExpenses) {index, item ->
                    ExpenseCard(
                        item = item,
                        modifier = Modifier,
                        onEditClick = {},
                        onDeleteClick = {},
                    )
                }
            }
            OutlinedTextField(
                value = uiState.prompt,
                onValueChange = viewModel::onPromptChange,
                enabled = !uiState.isLoading,
                label = {Text("Seu Pedido")},
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                trailingIcon = {
                    if(uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        IconButton(
                            onClick = viewModel::submitPrompt,
                            enabled = uiState.prompt.isNotBlank()
                        ) {
                            Icon(Icons.Default.Send, "Enviar" )
                        }
                    }
                }
            )
        }
    }
}