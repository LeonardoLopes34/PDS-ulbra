package com.example.controlegasto.presentation.screens.configurations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlegasto.presentation.theme.ControleGastoTheme
import com.example.controlegasto.presentation.theme.LightBlue
import com.example.controlegasto.presentation.theme.LightBlue2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(modifier: Modifier = Modifier) {
    val borderColor = MaterialTheme.colorScheme.surfaceTint
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val navItems = listOf( //placeholder da bottom
        "Inicio" to Icons.Default.Home,
        "Relatorios" to Icons.Default.Search,
        "Configurações" to Icons.Default.Settings
    )
    Scaffold(
        containerColor = LightBlue2,
        modifier = Modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBlue,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {Text("Configurações")},
                navigationIcon = {
                    IconButton(onClick = {/* TODO */}) {
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
                        label = { Text(text = item.first) }
                    )
                }
            }
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
                        .drawBehind{
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
                    onClick = {/* TODO  */}
                    //receber a cor pela cor da categoria
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