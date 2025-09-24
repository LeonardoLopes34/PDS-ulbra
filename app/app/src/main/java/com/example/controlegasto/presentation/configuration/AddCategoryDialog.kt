package com.example.controlegasto.presentation.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.AlertDialog // <-- A importação correta!
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.controlegasto.domain.entities.Category

@Composable
fun CategoryAddDialog(
    categoryToEdit: Category?,
    onDismissRequest: () -> Unit,
    onSaveCategory: (name: String, color: Color) -> Unit
) {
    var categoryName by remember ( categoryToEdit ) {mutableStateOf(categoryToEdit?.name ?: "")}
    val colorOptions = listOf(
        Color(0xFFFD1303), Color(0xFF1D6200), Color(0xFFD501FC), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFFEAE00E), Color(0xFF00BCD4), Color(0xFF7EF581),
        Color(0xFFFF9800), Color(0xFF62463C)
    )
    var selectedColor by remember(categoryToEdit) {
        mutableStateOf(
            if (categoryToEdit != null) Color(categoryToEdit.color.toULong())
            else colorOptions.first()
        )
    }

    val dialogTitle = if (categoryToEdit == null) "Nova Categoria" else "Editar Categoria"

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(dialogTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Nome da Categoria") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Column {
                    Text("Cor", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(colorOptions) { color ->
                            ColorCircle(
                                color = color,
                                isSelected = (color == selectedColor),
                                onClick = { selectedColor = color }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onSaveCategory(categoryName, selectedColor)
                    }
                },
                enabled = categoryName.isNotBlank()
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = CircleShape
            )

    )
}

