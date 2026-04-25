package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.components.TrainingExerciseCard
import com.scribblefit.feature.canvas.ui.CanvasState
import com.scribblefit.feature.canvas.ui.ScribbleUiModel

@Composable
internal fun LoggedScribbleCard(
    state: CanvasState,
    scribble: ScribbleUiModel,
    onClick: () -> Unit,
    onExerciseClick: (Long) -> Unit
) {
    ScribbleCardContainer(
        onClick = onClick,
        alpha = 0.8f
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScribbleRawText(text = scribble.rawText)
                ScribbleStatusBadge(text = state.getBadgeText(scribble.status), showIcon = true)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
            ) {
                scribble.exercises.forEachIndexed { index, exercise ->
                    TrainingExerciseCard(
                        name = exercise.name,
                        formattedSummary = exercise.formattedSummary,
                        estimated1RM = state.getEstimated1RM(exercise),
                        intensity = state.getIntensity(exercise),
                        improvement = state.getImprovement(exercise),
                        hasStats = exercise.hasStats,
                        onClick = { onExerciseClick(exercise.id) },
                        fontSize = 28,
                        kerning = -1.0
                    )

                    if (index < scribble.exercises.size - 1) {
                        HorizontalDivider(
                            color = ScribbleFitTheme.colors.primary.copy(alpha = 0.1f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}
