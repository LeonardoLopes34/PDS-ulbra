package com.example.controlegasto.presentation.aiAnalysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
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
                    text = "Faça uma pergunta sobre seus gastos",
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                //TODO List with AI filtered expenses
            }
            OutlinedTextField(
                value = uiState.prompt,
                onValueChange = viewModel::onPromptChange,
                label = {Text("Seu Pedido")},
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                trailingIcon = {
                    IconButton(onClick = {viewModel.submitPrompt()}) {
                        Icon(Icons.Default.Send, "Enviar")
                    }
                }
            )
        }

    }
}