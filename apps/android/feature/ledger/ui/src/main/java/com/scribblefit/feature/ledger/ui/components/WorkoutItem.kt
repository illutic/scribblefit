package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.scribbleGlass
import com.scribblefit.core.model.Workout

@Composable
internal fun WorkoutItem(
    workout: Workout,
    dateHeader: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .scribbleGlass(cornerRadius = 16.dp),
        color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dateHeader,
                    style = ScribbleFitTheme.typography.titleMedium,
                    color = ScribbleFitTheme.colors.primary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.midGray,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            
            workout.exercises.forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exercise.canonicalName,
                        style = ScribbleFitTheme.typography.bodyMedium,
                        color = ScribbleFitTheme.colors.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatMetrics(exercise),
                        style = ScribbleFitTheme.typography.labelMedium,
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
