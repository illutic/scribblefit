package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.canvas.ui.CanvasState

@Composable
internal fun ExerciseEditItem(
    exercise: Exercise,
    state: CanvasState,
    onUpdateExerciseName: (Long, String) -> Unit,
    onUpdateSetWeight: (Long, Long, String) -> Unit,
    onUpdateSetReps: (Long, Long, String) -> Unit,
    onDeleteSet: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                ScribbleFitTheme.colors.surfaceContainerLow,
                RoundedCornerShape(ScribbleFitTheme.spacing.smallLarger)
            )
            .padding(ScribbleFitTheme.spacing.medium)
    ) {
        BasicTextField(
            value = exercise.canonicalName,
            onValueChange = { onUpdateExerciseName(exercise.id, it) },
            textStyle = ScribbleFitTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = ScribbleFitTheme.colors.primary
            ),
            cursorBrush = SolidColor(ScribbleFitTheme.colors.primary),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.smallLarger))
        exercise.sets.forEach { set ->
            SetEditRow(
                exerciseId = exercise.id,
                set = set,
                state = state,
                onUpdateSetWeight = onUpdateSetWeight,
                onUpdateSetReps = onUpdateSetReps,
                onDeleteSet = onDeleteSet
            )
        }
    }
}
