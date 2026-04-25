package com.scribblefit.feature.insights.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun VolumeChart(
    points: List<VolumeDataPoint>,
    modifier: Modifier = Modifier,
) {
    if (points.isEmpty()) return

    val density = LocalDensity.current
    val lineColor = ScribbleFitTheme.colors.primary
    val fillColorTop = ScribbleFitTheme.colors.primary.copy(alpha = 0.12f)
    val fillColorBottom = ScribbleFitTheme.colors.primary.copy(alpha = 0.0f)
    val dotColor = ScribbleFitTheme.colors.primary
    val haloColor = ScribbleFitTheme.colors.surfaceContainerLow // Match background for contrast
    val labelColor = ScribbleFitTheme.colors.midGray

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 10.sp, color = labelColor)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d") }

    val maxVolume = points.maxOf { it.volume }
    val minVolume = points.minOf { it.volume }
    val midVolume = (maxVolume + minVolume) / 2f
    val range = (maxVolume - minVolume).coerceAtLeast(1f) // Ensure no division by zero

    fun formatAxisValue(value: Float): String {
        return when {
            value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000f)
            value >= 1000 -> String.format("%.1fk", value / 1000f)
            else -> String.format("%.0f", value)
        }
    }

    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLow,
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(ScribbleFitTheme.spacing.medium)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val yAxisWidth = with(density) { 48.dp.toPx() } // Slightly wider for large numbers
                val xAxisHeight = with(density) { 24.dp.toPx() }
                val dotRadius = with(density) { 4.5.dp.toPx() }
                val haloRadius = with(density) { 6.5.dp.toPx() }
                val strokeWidth = with(density) { 2.5.dp.toPx() }

                val chartLeft = yAxisWidth
                val chartRight = size.width - with(density) { 8.dp.toPx() } // Right margin for dot
                val chartTop = with(density) { 12.dp.toPx() } // Top margin for dot
                val chartBottom = size.height - xAxisHeight
                val chartWidth = (chartRight - chartLeft).coerceAtLeast(1f)
                val chartHeight = (chartBottom - chartTop).coerceAtLeast(1f)

                // Y-axis labels
                val yLabels = if (maxVolume > minVolume) {
                    listOf(
                        maxVolume to chartTop,
                        midVolume to chartTop + chartHeight / 2f,
                        minVolume to chartBottom
                    )
                } else {
                    listOf(maxVolume to chartTop + chartHeight / 2f)
                }

                for ((value, y) in yLabels) {
                    val text = formatAxisValue(value)
                    val measured = textMeasurer.measure(text, labelStyle)
                    drawText(
                        textLayoutResult = measured,
                        topLeft = Offset(
                            x = (yAxisWidth - measured.size.width - with(density) { 12.dp.toPx() }).coerceAtLeast(
                                0f
                            ),
                            y = y - measured.size.height / 2f
                        )
                    )
                }

                fun pointOffset(index: Int): Offset {
                    val x = if (points.size > 1) {
                        chartLeft + (index.toFloat() / (points.size - 1)) * chartWidth
                    } else {
                        chartLeft + chartWidth / 2f
                    }

                    val normalizedY = if (maxVolume > minVolume) {
                        (points[index].volume - minVolume) / range
                    } else {
                        0.5f
                    }
                    val y = chartBottom - normalizedY * chartHeight
                    return Offset(x, y)
                }

                if (points.size >= 2) {
                    val linePath = Path().apply {
                        val first = pointOffset(0)
                        moveTo(first.x, first.y)
                        for (i in 1 until points.size) {
                            val prev = pointOffset(i - 1)
                            val curr = pointOffset(i)
                            val cpX1 = prev.x + (curr.x - prev.x) / 2f
                            val cpX2 = prev.x + (curr.x - prev.x) / 2f
                            cubicTo(cpX1, prev.y, cpX2, curr.y, curr.x, curr.y)
                        }
                    }

                    // Fill
                    val fillPath = Path().apply {
                        addPath(linePath)
                        lineTo(pointOffset(points.size - 1).x, chartBottom)
                        lineTo(pointOffset(0).x, chartBottom)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(fillColorTop, fillColorBottom),
                            startY = chartTop,
                            endY = chartBottom
                        )
                    )

                    // Line
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Dots with halo
                for (i in points.indices) {
                    val p = pointOffset(i)
                    drawCircle(color = haloColor, radius = haloRadius, center = p)
                    drawCircle(color = dotColor, radius = dotRadius, center = p)
                }

                // X-axis date labels
                val xLabelIndices = when {
                    points.size <= 3 -> points.indices.toList()
                    else -> listOf(0, points.size / 2, points.size - 1)
                }.distinct()

                for (i in xLabelIndices) {
                    val date = Instant.ofEpochMilli(points[i].date)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    val text = dateFormatter.format(date)
                    val measured = textMeasurer.measure(text, labelStyle)
                    val p = pointOffset(i)
                    val labelX = (p.x - measured.size.width / 2f)
                        .coerceIn(
                            chartLeft - measured.size.width / 2f,
                            size.width - measured.size.width
                        )
                    drawText(
                        textLayoutResult = measured,
                        topLeft = Offset(labelX, chartBottom + with(density) { 6.dp.toPx() })
                    )
                }
            }
        }
    }
}
