package com.scribblefit.feature.insights.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import java.time.format.DateTimeFormatter

@Composable
internal fun VolumeChart(
    points: List<VolumeDataPoint>,
    modifier: Modifier = Modifier,
) {
    if (points.size < 2) return

    val lineColor = ScribbleFitTheme.colors.primary
    val fillColorTop = ScribbleFitTheme.colors.primary.copy(alpha = 0.15f)
    val fillColorBottom = ScribbleFitTheme.colors.primary.copy(alpha = 0.0f)
    val dotColor = ScribbleFitTheme.colors.primary
    val labelColor = ScribbleFitTheme.colors.midGray

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 10.sp, color = labelColor)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d") }

    // Pre-compute Y-axis labels
    val maxVolume = points.maxOf { it.volume }
    val minVolume = points.minOf { it.volume }
    val midVolume = (maxVolume + minVolume) / 2f

    fun formatAxisValue(value: Float): String {
        return when {
            value >= 1_000_000 -> String.format("%.0fM", value / 1_000_000f)
            value >= 1000 -> String.format("%.0fk", value / 1000f)
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
                val yAxisWidth = 36f
                val xAxisHeight = 20f
                val chartLeft = yAxisWidth
                val chartRight = size.width
                val chartTop = 8f
                val chartBottom = size.height - xAxisHeight
                val chartWidth = chartRight - chartLeft
                val chartHeight = chartBottom - chartTop
                val range = (maxVolume - minVolume).coerceAtLeast(1f)

                // Y-axis labels (top, mid, bottom)
                val yLabels = listOf(maxVolume to chartTop, midVolume to chartTop + chartHeight / 2f, minVolume to chartBottom)
                for ((value, y) in yLabels) {
                    val text = formatAxisValue(value)
                    val measured = textMeasurer.measure(text, labelStyle)
                    drawText(
                        textLayoutResult = measured,
                        topLeft = Offset(0f, y - measured.size.height / 2f)
                    )
                }

                // Chart data points
                fun pointOffset(index: Int): Offset {
                    val x = chartLeft + (index.toFloat() / (points.size - 1).coerceAtLeast(1)) * chartWidth
                    val normalizedY = (points[index].volume - minVolume) / range
                    val y = chartBottom - normalizedY * chartHeight
                    return Offset(x, y)
                }

                // Smooth cubic path
                val linePath = Path().apply {
                    val first = pointOffset(0)
                    moveTo(first.x, first.y)
                    for (i in 1 until points.size) {
                        val prev = pointOffset(i - 1)
                        val curr = pointOffset(i)
                        val cpX = (prev.x + curr.x) / 2f
                        cubicTo(cpX, prev.y, cpX, curr.y, curr.x, curr.y)
                    }
                }

                // Fill area
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

                // Line stroke
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Dots
                for (i in points.indices) {
                    val p = pointOffset(i)
                    drawCircle(color = dotColor, radius = 4f, center = p)
                }

                // X-axis date labels (first, middle, last)
                val xLabelIndices = listOf(0, points.size / 2, points.size - 1).distinct()
                for (i in xLabelIndices) {
                    val text = points[i].date.format(dateFormatter)
                    val measured = textMeasurer.measure(text, labelStyle)
                    val p = pointOffset(i)
                    val labelX = (p.x - measured.size.width / 2f)
                        .coerceIn(chartLeft, chartRight - measured.size.width)
                    drawText(
                        textLayoutResult = measured,
                        topLeft = Offset(labelX, chartBottom + 4f)
                    )
                }
            }
        }
    }
}
