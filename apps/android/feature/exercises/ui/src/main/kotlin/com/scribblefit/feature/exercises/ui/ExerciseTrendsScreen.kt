package com.scribblefit.feature.exercises.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.SegmentedSelector
import com.scribblefit.core.designsystem.TopBar
import com.scribblefit.core.model.TrendDirection
import com.scribblefit.feature.exercises.domain.usecase.TrendDataPoint
import com.scribblefit.feature.exercises.domain.usecase.TrendInsights
import com.scribblefit.feature.exercises.domain.usecase.TrendPeriod
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ExerciseTrendsScreen(
    viewModel: ExerciseTrendsViewModel
) {
    val state by viewModel.state.collectAsState()

    ExerciseTrendsContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
private fun ExerciseTrendsContent(
    state: ExerciseTrendsState,
    onIntent: (ExerciseTrendsIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = state.navigationTitle,
                        style = ScribbleFitTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(ExerciseTrendsIntent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = ScribbleFitTheme.colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        containerColor = ScribbleFitTheme.colors.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.oneRMDataPoints.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ScribbleFitTheme.colors.primary
                )
            } else if (state.oneRMDataPoints.isEmpty() && state.volumeDataPoints.isEmpty() && !state.isLoading) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ShowChart,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = ScribbleFitTheme.colors.midGray
                    )
                    Text(
                        text = state.emptyDataMessage,
                        style = ScribbleFitTheme.typography.titleMedium,
                        color = ScribbleFitTheme.colors.midGray
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = ScribbleFitTheme.spacing.screenPadding)
                        .padding(bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Spacer(modifier = Modifier.height(0.dp))

                    val periodOptions = remember {
                        listOf(
                            TrendPeriod.ONE_MONTH to "1M",
                            TrendPeriod.THREE_MONTHS to "3M",
                            TrendPeriod.SIX_MONTHS to "6M",
                            TrendPeriod.ONE_YEAR to "1Y",
                            TrendPeriod.ALL to "All"
                        )
                    }

                    SegmentedSelector(
                        options = periodOptions,
                        selectedOption = state.selectedPeriod,
                        onOptionSelected = { onIntent(ExerciseTrendsIntent.UpdatePeriod(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TrendChartSection(
                        title = state.oneRMSectionTitle,
                        data = state.oneRMDataPoints,
                        insights = state.oneRMInsights,
                        unit = state.weightUnitLabel,
                        pbLabel = state.personalBestLabel,
                        getTrendBadgeText = { dir, p -> state.getTrendBadgeText(dir, p) }
                    )

                    TrendChartSection(
                        title = state.volumeSectionTitle,
                        data = state.volumeDataPoints,
                        insights = state.volumeInsights,
                        unit = state.weightUnitLabel,
                        pbLabel = state.personalBestLabel,
                        getTrendBadgeText = { dir, p -> state.getTrendBadgeText(dir, p) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendChartSection(
    title: String,
    data: List<TrendDataPoint>,
    insights: TrendInsights?,
    unit: String,
    pbLabel: String,
    getTrendBadgeText: @Composable (TrendDirection, Float) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                style = ScribbleFitTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = ScribbleFitTheme.colors.midGray,
                letterSpacing = 1.sp
            )

            if (insights != null) {
                TrendBadge(
                    direction = insights.trendDirection,
                    text = getTrendBadgeText(insights.trendDirection, insights.percentageChange)
                )
            }
        }

        Surface(
            color = ScribbleFitTheme.colors.surfaceContainerLow,
            shape = RoundedCornerShape(ScribbleFitTheme.shapes.large)
        ) {
            Column(modifier = Modifier.padding(ScribbleFitTheme.spacing.large)) {
                if (data.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = data.last().value.toInt().toString(),
                                style = ScribbleFitTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = ScribbleFitTheme.colors.primary
                            )
                            Text(
                                text = unit,
                                style = ScribbleFitTheme.typography.bodyMedium,
                                color = ScribbleFitTheme.colors.midGray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        if (insights != null) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = pbLabel,
                                    style = TextStyle(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ScribbleFitTheme.colors.midGray
                                )
                                Text(
                                    text = "${insights.personalBest.toInt()} $unit",
                                    style = ScribbleFitTheme.typography.labelSmall,
                                    color = ScribbleFitTheme.colors.primary
                                )
                            }
                        }
                    }
                }

                TrendChart(
                    data = data,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun TrendBadge(
    direction: TrendDirection,
    text: String
) {
    val isPositive = direction == TrendDirection.IMPROVING || direction == TrendDirection.STABLE

    Surface(
        color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.1f),
        shape = CircleShape
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = if (isPositive) ScribbleFitTheme.colors.primary else ScribbleFitTheme.colors.midGray
        )
    }
}

@Composable
private fun TrendChart(
    data: List<TrendDataPoint>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val density = LocalDensity.current
    val lineColor = ScribbleFitTheme.colors.primary
    val fillColorTop = ScribbleFitTheme.colors.primary.copy(alpha = 0.12f)
    val fillColorBottom = ScribbleFitTheme.colors.primary.copy(alpha = 0.0f)
    val dotColor = ScribbleFitTheme.colors.primary
    val haloColor = ScribbleFitTheme.colors.surfaceContainerLow
    val labelColor = ScribbleFitTheme.colors.midGray

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 10.sp, color = labelColor)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d") }

    val maxValue = data.maxOf { it.value }
    val minValue = data.minOf { it.value }
    val range = (maxValue - minValue).coerceAtLeast(1f)

    Canvas(modifier = modifier) {
        val yAxisWidth = with(density) { 40.dp.toPx() }
        val xAxisHeight = with(density) { 24.dp.toPx() }
        val dotRadius = with(density) { 4.5.dp.toPx() }
        val haloRadius = with(density) { 6.5.dp.toPx() }
        val strokeWidth = with(density) { 2.5.dp.toPx() }

        val chartLeft = yAxisWidth
        val chartRight = size.width - 8.dp.toPx()
        val chartTop = 12.dp.toPx()
        val chartBottom = size.height - xAxisHeight
        val chartWidth = (chartRight - chartLeft).coerceAtLeast(1f)
        val chartHeight = (chartBottom - chartTop).coerceAtLeast(1f)

        // Draw Y-axis labels (min/max)
        val yLabels = if (maxValue > minValue) {
            listOf(maxValue to chartTop, minValue to chartBottom)
        } else {
            listOf(maxValue to chartTop + chartHeight / 2f)
        }

        for ((value, y) in yLabels) {
            val text = value.toInt().toString()
            val measured = textMeasurer.measure(text, labelStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    x = (yAxisWidth - measured.size.width - 8.dp.toPx()).coerceAtLeast(0f),
                    y = y - measured.size.height / 2f
                )
            )
        }

        fun pointOffset(index: Int): Offset {
            val x = if (data.size > 1) {
                chartLeft + (index.toFloat() / (data.size - 1)) * chartWidth
            } else {
                chartLeft + chartWidth / 2f
            }
            val normalizedY =
                if (maxValue > minValue) (data[index].value - minValue) / range else 0.5f
            val y = chartBottom - normalizedY * chartHeight
            return Offset(x, y)
        }

        if (data.size >= 2) {
            val linePath = Path().apply {
                val first = pointOffset(0)
                moveTo(first.x, first.y)
                for (i in 1 until data.size) {
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
                lineTo(pointOffset(data.size - 1).x, chartBottom)
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
        for (i in data.indices) {
            val p = pointOffset(i)
            drawCircle(color = haloColor, radius = haloRadius, center = p)
            drawCircle(color = dotColor, radius = dotRadius, center = p)
        }

        // X-axis date labels (start/end)
        val xIndices = if (data.size >= 2) listOf(0, data.size - 1) else listOf(0)
        for (i in xIndices) {
            val date =
                Instant.ofEpochMilli(data[i].date).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val text = dateFormatter.format(date)
            val measured = textMeasurer.measure(text, labelStyle)
            val p = pointOffset(i)
            val labelX = (p.x - measured.size.width / 2f).coerceIn(
                chartLeft,
                size.width - measured.size.width
            )
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(labelX, chartBottom + 6.dp.toPx())
            )
        }
    }
}
