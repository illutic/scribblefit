package com.scribblefit.feature.exercises.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.ExerciseTrends
import com.scribblefit.core.model.TrendDirection

@Composable
fun TrendsSection(
    trends: ExerciseTrends,
    weightUnit: String,
    titleLabel: String,
    viewAllLabel: String,
    current1rmLabel: String,
    intensityLabel: String,
    weightVsLastLabel: String,
    lastVolumeLabel: String,
    onViewAllClick: () -> Unit,
    getTrendDirectionText: @Composable (TrendDirection) -> String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titleLabel,
                style = ScribbleFitTheme.typography.labelSmall,
                color = ScribbleFitTheme.colors.midGray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            TextButton(onClick = onViewAllClick) {
                Text(
                    text = viewAllLabel,
                    style = ScribbleFitTheme.typography.labelSmall,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Column(
            modifier = Modifier.padding(top = ScribbleFitTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.smallLarger)
        ) {
            TrendItem(
                label = current1rmLabel,
                value = "${trends.estimated1RM.toInt()}$weightUnit",
                direction = trends.trendDirection,
                getTrendDirectionText = getTrendDirectionText
            )

            TrendItem(
                label = intensityLabel,
                value = "${(trends.intensity * 100).toInt()}%",
                direction = TrendDirection.STABLE,
                getTrendDirectionText = getTrendDirectionText
            )

            TrendItem(
                label = weightVsLastLabel,
                value = "${if (trends.improvement >= 0) "+" else ""}${(trends.improvement * 100).toInt()}%",
                direction = trends.trendDirection,
                getTrendDirectionText = getTrendDirectionText
            )

            TrendItem(
                label = lastVolumeLabel,
                value = "${trends.lastVolume.toInt()}$weightUnit",
                direction = trends.lastVolumeTrend,
                getTrendDirectionText = getTrendDirectionText
            )
        }
    }
}

@Composable
private fun TrendItem(
    label: String,
    value: String,
    direction: TrendDirection,
    getTrendDirectionText: @Composable (TrendDirection) -> String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = label,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = ScribbleFitTheme.typography.bodyLarge,
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(ScribbleFitTheme.spacing.smallLarger))

            TrendBadge(
                direction = direction,
                text = getTrendDirectionText(direction)
            )
        }
    }
}

@Composable
private fun TrendBadge(direction: TrendDirection, text: String) {
    val color = when (direction) {
        TrendDirection.IMPROVING -> ScribbleFitTheme.colors.successGreen
        TrendDirection.STABLE -> ScribbleFitTheme.colors.midGray
        TrendDirection.PLATEAUED -> ScribbleFitTheme.colors.warningOrange
        TrendDirection.DECLINING -> ScribbleFitTheme.colors.dangerRed
    }

    Text(
        text = text,
        style = ScribbleFitTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
