package com.example.controlegasto.presentation.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlegasto.domain.entities.Category
import com.example.controlegasto.domain.entities.Expense
import com.example.controlegasto.domain.entities.PaymentMethod
import com.example.controlegasto.presentation.reports.ExpenseWithCategory
import com.example.controlegasto.presentation.theme.ControleGastoTheme
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

@Composable
fun ExpenseCard(
    modifier: Modifier = Modifier,
    item: ExpenseWithCategory,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isSelected: Boolean = false,

) {

    val currency = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(Color(item.category.color.toULong()))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = currency.format(item.expense.value),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                AssistChip(
                    onClick = {/* TODO */ },
                    label = { Text(item.category.name, fontWeight = FontWeight.Bold) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), /*TODO cor teste susbtiuir pela cor da categoria depois */
                        labelColor = Color.White
                    )
                )
            }
            if(item.expense.description.isNotBlank()){
                Text(
                    text = item.expense.description,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEditClick,
                    modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar Despesa"
                    )
                }
                IconButton(onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Deletar Despesa"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpanseCardPreview(modifier: Modifier = Modifier) {
    val testCategory = Category(id = 1, name = "Pessoal", color = Color.Red.value.toLong())
    val testExpense = Expense(
        id = 1,
        value = BigDecimal("230.20"),
        description = "Compra de teste para o preview do card",
        categoryId = 1,
        paymentMethod = PaymentMethod.CREDIT_CARD,
        date = LocalDate.now()
    )
    val testItem = ExpenseWithCategory(expense = testExpense, category = testCategory)

    ControleGastoTheme {
        ExpenseCard(
            item = testItem,
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}