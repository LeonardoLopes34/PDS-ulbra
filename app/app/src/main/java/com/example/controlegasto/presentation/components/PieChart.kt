package com.example.controlegasto.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.controlegasto.presentation.reports.PieChartData
import java.util.Locale

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("Sem dados para exibir")
        }
        return
    }

    val total = remember(data) { data.sumOf { it.totalValue.toDouble() }.toFloat() }
    val proportions = remember(data) { data.map { it.totalValue / total } }
    val sweepAngles = remember(proportions) { proportions.map { it * 360f } }

    Canvas(modifier = modifier) {
        val diameter = minOf(size.width, size.height)
        val topLeft = Offset(
            x = (size.width - diameter) / 2f,
            y = (size.height - diameter) / 2f
        )

        val strokeWidth = diameter * 0.2f

        var startAngle = -90f
        data.forEachIndexed { index, slice ->
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngles[index],
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweepAngles[index]
        }
    }
}


@Composable
fun ChartLegend(
    data: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        items(data) { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(item.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val legendText = "${item.categoryName} (${String.format(Locale.getDefault(), "%.1f", item.percentage)}%)"
                Text(
                    text = legendText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}