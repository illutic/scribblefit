package com.scribblefit.feature.canvas.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.InsightType
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.canvas.ui.CanvasIntent
import com.scribblefit.feature.canvas.ui.CanvasState
import com.scribblefit.feature.canvas.ui.ScribbleUiModel
import com.scribblefit.feature.canvas.ui.components.card.ScribbleCard

@Composable
internal fun CanvasBody(
    state: CanvasState,
    onScribbleClick: (Scribble) -> Unit,
    onExerciseClick: (exerciseName: String) -> Unit,
    onIntent: (CanvasIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val aiInsights = state.aiInsights
    val isGeneratingInsights = state.isGeneratingInsights
    val scribbles = state.scribbleUiModels
    val emptyText = state.emptyScribbleText

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = ScribbleFitTheme.spacing.large,
            end = ScribbleFitTheme.spacing.large,
            top = ScribbleFitTheme.spacing.large,
            bottom = 120.dp
        ),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
    ) {
        if (isGeneratingInsights) {
            item {
                AIInsightsLoadingSection()
            }
        } else if (aiInsights.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)) {
                    aiInsights.forEach { insight ->
                        AIInsightCard(insight = insight)
                    }
                }
            }
        }

        if (scribbles.isEmpty()) {
            item {
                EmptyCanvasContent(text = emptyText)
            }
        } else {
            items(scribbles, key = { it.id }) { scribble ->
                ScribbleCard(
                    state = state,
                    scribble = scribble,
                    onClick = { onScribbleClick(scribble.scribble) },
                    onExerciseClick = onExerciseClick,
                    onIntent = onIntent
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier
                    .imePadding()
                    .padding(40.dp)
            )
        }
    }
}

@Composable
private fun AIInsightsLoadingSection() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
        modifier = Modifier.alpha(alpha)
    ) {
        repeat(1) {
            Surface(
                color = ScribbleFitTheme.colors.surfaceContainerLow,
                shape = RoundedCornerShape(ScribbleFitTheme.spacing.medium),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                ScribbleFitTheme.colors.primary.copy(alpha = 0.1f),
                                CircleShape
                            )
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)) {
                        Box(
                            modifier = Modifier
                                .height(ScribbleFitTheme.spacing.smallLarger)
                                .fillMaxWidth(0.3f)
                                .background(
                                    ScribbleFitTheme.colors.midGray.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .height(ScribbleFitTheme.spacing.medium)
                                .fillMaxWidth(0.9f)
                                .background(
                                    ScribbleFitTheme.colors.primary.copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .height(ScribbleFitTheme.spacing.medium)
                                .fillMaxWidth(0.6f)
                                .background(
                                    ScribbleFitTheme.colors.primary.copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AIInsightCard(insight: AIInsight) {
    var expanded by remember { mutableStateOf(false) }

    val (icon, color) = when (insight.insightType) {
        InsightType.SUMMARY -> Icons.Rounded.AutoAwesome to ScribbleFitTheme.colors.primary
        InsightType.TREND -> Icons.Rounded.ShowChart to ScribbleFitTheme.colors.primary
        InsightType.ADVICE -> Icons.Rounded.Lightbulb to ScribbleFitTheme.colors.primary
    }

    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLow,
        shape = RoundedCornerShape(ScribbleFitTheme.spacing.medium),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        onClick = { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = insight.insightType.name,
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = insight.text,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyCanvasContent(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = ScribbleFitTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = ScribbleFitTheme.colors.midGray,
            lineHeight = 32.sp
        )
    }
}
