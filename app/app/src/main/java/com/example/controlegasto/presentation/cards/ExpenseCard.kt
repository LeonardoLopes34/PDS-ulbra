package com.example.controlegasto.presentation.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlegasto.presentation.theme.ControleGastoTheme
import com.example.controlegasto.presentation.theme.LightBlue2
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpenseCard(
    expanseNumeration: Int,
    expanseTotal: BigDecimal,
    description: String,    // receber cor e nome da categoria de uma possivel dataclass da categoria
    category: String,
    modifier: Modifier = Modifier
) {

    val currency = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Card(
        modifier = Modifier.defaultMinSize(80.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(LightBlue2)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "N.${expanseNumeration}",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = currency.format(expanseTotal),
                    fontSize = 25.sp,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black
                )
                AssistChip(
                    onClick = {/* TODO */ },
                    label = { Text(category, fontWeight = FontWeight.Bold) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.error, //cor teste susbtiuir pela cor da categoria depois
                        labelColor = Color.White
                    )
                )
            }
            Text(
                text = description,
                fontSize = 20.sp,
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {/* TODO */},
                    modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar Despesa"
                    )
                }
                IconButton(onClick = {/* TODO */},
                    modifier = Modifier.size(28.dp)) {
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
    ControleGastoTheme {
        ExpenseCard(1, BigDecimal("230.20"), "compra Teste", "Pessoal" )
    }
}