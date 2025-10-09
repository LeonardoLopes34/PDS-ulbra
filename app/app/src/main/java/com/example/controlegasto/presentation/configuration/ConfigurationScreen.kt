package com.example.controlegasto.presentation.configuration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controlegasto.presentation.theme.ControleGastoTheme
import com.example.controlegasto.presentation.theme.LightBlue2
import com.example.controlegasto.presentation.theme.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    modifier: Modifier = Modifier,
    viewModel: ConfigurationViewModel = viewModel(factory = ConfigurationViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categoryList.collectAsState()


    val borderColor = MaterialTheme.colorScheme.surfaceTint

    if(uiState.isAddCategoryDialogVisible) {
        CategoryAddDialog(
            categoryToEdit = uiState.categoryToEdit,
            onDismissRequest = viewModel::onAddCategoryDismiss,
            onSaveCategory = {name, selectedColor ->
                viewModel.onSaveCategory(name, selectedColor)
                viewModel.onAddCategoryDismiss()
            }
        )
    }

    val categoryToDelete = uiState.categoryForDeletion
    if (categoryToDelete != null) {
        DeleteCategoryConfirmationDialog(
            categoryName = categoryToDelete.name,
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::cancelDelete
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
                title = {Text("Configurações")},
                navigationIcon = {
                    IconButton(onClick = {/* TODO button to go back to mainscreen*/}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icone de voltar"
                        )
                    }
                }
            )
        },
        content = {innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),


            ) {
                Text(
                    "Categorias",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 50.dp, start = 30.dp)
                )
                // lazycolumn para exibir todas as categorias com um botao para excluir
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .drawBehind {
                            val strokeWidth = Stroke.DefaultMiter
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f), 0f)
                            val cornerRadius = CornerRadius(12.dp.toPx())
                            drawRoundRect(
                                color = borderColor,
                                style = Stroke(
                                    width = strokeWidth,
                                    pathEffect = pathEffect
                                ),
                                cornerRadius = cornerRadius
                            )
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = viewModel::onAddCategoryClicked
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Icone para adicionar Categoria"
                        )
                        Text(
                            "Adicionar Categoria"
                        )
                    }
                }
                LazyColumn(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        CategoryCard(
                            categoryName = category.name,
                            categoryColor = Color(category.color.toULong()),
                            modifier = Modifier.fillMaxWidth(),
                            onDeleteClick = { viewModel.requestDeleteConfirmation(category)},
                            onEditClick = { viewModel.onEditCategory(category)}
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun DeleteCategoryConfirmationDialog(
    categoryName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Exclusão") },
        text = {
            Text("Tem a certeza de que deseja apagar a categoria \"$categoryName\"? Esta ação não pode ser desfeita.")
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
fun ConfigurationScreenPreview(modifier: Modifier = Modifier) {
    ControleGastoTheme {
        ConfigurationScreen()
    }
}