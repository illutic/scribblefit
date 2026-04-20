package com.scribblefit.feature.insights.ui.components

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
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.insights.ui.InsightsState
import com.scribblefit.feature.insights.ui.getExercisesLabel
import com.scribblefit.feature.insights.ui.getSessionsLabel
import com.scribblefit.feature.insights.ui.getTotalVolumeLabel

@Composable
internal fun StatsRow(state: InsightsState) {
    val frequency = state.frequency ?: return
    val totalVolume = state.volumePoints.sumOf { it.volume.toDouble() }.toInt()
    val exerciseCount = frequency.totalExercises

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small),
    ) {
        StatCard(
            value = frequency.totalWorkouts.toString(),
            label = state.getSessionsLabel(),
            modifier = Modifier.weight(1f),
        )
        StatCard(
            value = formatVolume(totalVolume),
            label = state.getTotalVolumeLabel(),
            modifier = Modifier.weight(1f),
        )
        StatCard(
            value = exerciseCount.toString(),
            label = state.getExercisesLabel(),
            modifier = Modifier.weight(1f),
        )
    }
}

private fun formatVolume(volume: Int): String {
    return when {
        volume >= 1_000_000 -> String.format("%.1fM", volume / 1_000_000.0)
        volume >= 1000 -> String.format("%.1fk", volume / 1000.0)
        else -> volume.toString()
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLow,
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = value,
                style = ScribbleFitTheme.typography.headlineSmall,
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = ScribbleFitTheme.typography.labelMedium,
                color = ScribbleFitTheme.colors.midGray,
            )
        }
    }
}
