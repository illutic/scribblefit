package com.scribblefit.feature.insights.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.ui.InsightsState
import com.scribblefit.feature.insights.ui.getMuscleDistributionTitle

@Composable
internal fun MuscleDistributionSection(state: InsightsState) {
    SectionContainer(title = state.getMuscleDistributionTitle()) {
        Surface(
            color = ScribbleFitTheme.colors.surfaceContainerLow,
            shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.smallLarger),
            ) {
                state.distribution.forEach { group ->
                    MuscleGroupBar(group = group)
                }
            }
        }
    }
}

@Composable
private fun MuscleGroupBar(group: MuscleGroupDistribution) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = group.muscleGroup,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "${group.percentage.toInt()}%",
                style = ScribbleFitTheme.typography.labelMedium,
                color = ScribbleFitTheme.colors.midGray,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(ScribbleFitTheme.colors.surfaceContainerHigh)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (group.percentage / 100f).coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(ScribbleFitTheme.colors.primary)
            )
        }
    }
}
