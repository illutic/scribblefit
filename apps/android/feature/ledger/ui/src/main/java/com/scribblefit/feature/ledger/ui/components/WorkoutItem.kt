package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.scribbleGlass
import com.scribblefit.core.model.Exercise

@Composable
internal fun WorkoutItem(
    title: String,
    exercises: List<Exercise>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(ScribbleFitTheme.spacing.medium),
        modifier = modifier
            .fillMaxWidth()
            .scribbleGlass(cornerRadius = ScribbleFitTheme.spacing.medium),
        color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.medium)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = ScribbleFitTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.primary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            exercises.forEachIndexed { index, exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exercise.canonicalName,
                        style = ScribbleFitTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ScribbleFitTheme.colors.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatMetrics(exercise),
                        style = ScribbleFitTheme.typography.labelSmall,
                        color = ScribbleFitTheme.colors.midGray
                    )
                }
            }
        }
    }
}

private fun formatMetrics(exercise: com.scribblefit.core.model.Exercise): String {
    val totalReps = exercise.sets.sumOf { it.reps }
    val totalSets = exercise.sets.size
    return "$totalSets sets · $totalReps reps"
}
