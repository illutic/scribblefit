package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.canvas.ui.ScribbleUiModel

@Composable
internal fun LoggedScribbleCard(
    scribble: ScribbleUiModel,
    onClick: () -> Unit
) {
    ScribbleCardContainer(
        onClick = onClick,
        alpha = 0.8f
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            ScribbleRawText(
                text = scribble.rawText,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.primary,
                modifier = Modifier
                    .padding(start = ScribbleFitTheme.spacing.medium)
                    .size(ScribbleFitTheme.spacing.mediumLarger)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)) {
            scribble.exercises.forEach { exercise ->
                Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)) {
                    ExerciseSummary(exercise.summary)
                    ExerciseStats(exercise)
                }
            }
        }
    }
}
