package com.scribblefit.feature.exercises.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.WeeklyStats

@Composable
fun WeeklyStatsCard(
    stats: WeeklyStats,
    weightUnit: String,
    titleLabel: String,
    activityLabel: String,
    sessionsUnitLabel: String,
    volumeLabel: String,
    maxWeightLabel: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.large),
        color = ScribbleFitTheme.colors.surfaceContainerLowest
    ) {
        Column(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.large)
        ) {
            Text(
                text = titleLabel,
                style = ScribbleFitTheme.typography.labelSmall,
                color = ScribbleFitTheme.colors.midGray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ScribbleFitTheme.spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = activityLabel,
                    value = "${stats.sessions}",
                    unit = sessionsUnitLabel
                )
                StatItem(
                    label = volumeLabel,
                    value = "${stats.totalVolume}",
                    unit = weightUnit
                )
                StatItem(
                    label = maxWeightLabel,
                    value = "${stats.maxWeight}",
                    unit = weightUnit
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    unit: String
) {
    Column {
        Text(
            text = label,
            style = ScribbleFitTheme.typography.labelSmall,
            color = ScribbleFitTheme.colors.midGray
        )
        Row(
            modifier = Modifier.padding(top = ScribbleFitTheme.spacing.extraSmall),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                style = ScribbleFitTheme.typography.headlineSmall,
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " $unit",
                style = ScribbleFitTheme.typography.labelSmall,
                color = ScribbleFitTheme.colors.midGray,
                modifier = Modifier.padding(bottom = ScribbleFitTheme.spacing.extraSmall)
            )
        }
    }
}
