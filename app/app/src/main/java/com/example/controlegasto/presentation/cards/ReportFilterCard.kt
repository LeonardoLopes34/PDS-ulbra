package com.example.controlegasto.presentation.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.controlegasto.presentation.theme.ControleGastoTheme

@Composable
fun ReportFilterCard(category: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier.defaultMinSize(minHeight = 40.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(color)
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = category,
                fontSize = 10.sp,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportFilterCardPreview() {
    ControleGastoTheme {
        ReportFilterCard("Mercado", MaterialTheme.colorScheme.secondary)
    }
}