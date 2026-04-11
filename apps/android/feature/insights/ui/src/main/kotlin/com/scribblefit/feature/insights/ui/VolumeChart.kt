package com.scribblefit.feature.insights.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint

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

    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLow,
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
        modifier = modifier,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScribbleFitTheme.spacing.medium)
        ) {
            val maxVolume = points.maxOf { it.volume }
            val minVolume = points.minOf { it.volume }
            val range = (maxVolume - minVolume).coerceAtLeast(1f)

            val stepX = size.width / (points.size - 1).coerceAtLeast(1)
            val paddingY = 20f

            fun pointOffset(index: Int): Offset {
                val x = index * stepX
                val normalizedY = (points[index].volume - minVolume) / range
                val y = size.height - paddingY - normalizedY * (size.height - paddingY * 2)
                return Offset(x, y)
            }

            // Build smooth cubic path
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

            // Fill area beneath curve
            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(pointOffset(points.size - 1).x, size.height)
                lineTo(pointOffset(0).x, size.height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(fillColorTop, fillColorBottom)
                )
            )

            // Stroke the line
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(
                    width = 2.5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw dots at data points
            for (i in points.indices) {
                val p = pointOffset(i)
                drawCircle(color = dotColor, radius = 4f, center = p)
            }
        }
    }
}
