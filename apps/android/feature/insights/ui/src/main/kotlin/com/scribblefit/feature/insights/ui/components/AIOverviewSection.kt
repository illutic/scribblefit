package com.scribblefit.feature.insights.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.InsightType
import com.scribblefit.feature.insights.ui.InsightsState
import com.scribblefit.feature.insights.ui.getUpdatedJustNow

@Composable
internal fun AIOverviewSection(state: InsightsState) {
    if (state.isGeneratingAI) {
        AIOverviewShimmer()
    } else {
        val insights = state.aiOverview?.insights ?: return
        val summary = insights.firstOrNull { it.insightType == InsightType.SUMMARY }
            ?: insights.firstOrNull()
            ?: return

        AIOverviewCard(
            text = summary.text,
            updatedText = state.getUpdatedJustNow(),
        )

        val remaining = insights.filter { it !== summary }
        if (remaining.isNotEmpty()) {
            Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.small))
            remaining.forEach { insight ->
                InsightChip(insight = insight)
                Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.small))
            }
        }
    }
}

@Composable
private fun AIOverviewCard(
    text: String,
    updatedText: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLow,
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.large),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.smallLarger),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "\uD83D\uDD25", fontSize = 20.sp)
                Text(
                    text = updatedText,
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray,
                )
            }
            Text(
                text = text,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun AIOverviewShimmer() {
    val infiniteTransition = rememberInfiniteTransition(label = "aiPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aiAlpha"
    )

    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLow,
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.large),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
    ) {
        Column(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.smallLarger),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(ScribbleFitTheme.spacing.large)
                        .background(
                            ScribbleFitTheme.colors.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .height(10.dp)
                        .fillMaxWidth(0.3f)
                        .background(
                            ScribbleFitTheme.colors.midGray.copy(alpha = 0.15f),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth()
                    .background(
                        ScribbleFitTheme.colors.primary.copy(alpha = 0.08f),
                        RoundedCornerShape(4.dp)
                    )
            )
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.6f)
                    .background(
                        ScribbleFitTheme.colors.primary.copy(alpha = 0.08f),
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
private fun InsightChip(
    insight: AIInsight,
    modifier: Modifier = Modifier,
) {
    val emoji = when (insight.insightType) {
        InsightType.SUMMARY -> "\uD83D\uDD25"
        InsightType.TREND -> "\uD83D\uDCC8"
        InsightType.ADVICE -> "\uD83D\uDCA1"
    }

    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLow,
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.smallLarger),
            verticalAlignment = Alignment.Top,
        ) {
            Surface(
                color = ScribbleFitTheme.colors.primary.copy(alpha = 0.08f),
                shape = CircleShape,
                modifier = Modifier.size(28.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 14.sp)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = insight.insightType.name,
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = insight.text,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}
