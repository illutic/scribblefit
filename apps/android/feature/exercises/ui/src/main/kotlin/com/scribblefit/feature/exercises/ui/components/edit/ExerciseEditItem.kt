package com.scribblefit.feature.exercises.ui.components.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Exercise

@Composable
fun ExerciseEditItem(
    exercise: Exercise,
    weightUnitLabel: String,
    setRepsSeparator: String,
    repsLabel: String,
    deleteSetContentDescription: String,
    addSetLabel: String,
    onUpdateExerciseName: (Long, String) -> Unit,
    onDeleteExercise: (Long) -> Unit,
    onUpdateSetWeight: (Long, Long, String) -> Unit,
    onUpdateSetReps: (Long, Long, String) -> Unit,
    onDeleteSet: (Long, Long) -> Unit,
    onAddSet: (Long) -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicTextField(
                value = exercise.canonicalName,
                onValueChange = { onUpdateExerciseName(exercise.id, it) },
                textStyle = ScribbleFitTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.primary
                ),
                cursorBrush = SolidColor(ScribbleFitTheme.colors.primary),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.midGray.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDeleteExercise(exercise.id) }
            )
        }

        Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.smallLarger))

        exercise.sets.forEach { set ->
            SetEditRow(
                set = set,
                weightUnitLabel = weightUnitLabel,
                setRepsSeparator = setRepsSeparator,
                repsLabel = repsLabel,
                deleteSetContentDescription = deleteSetContentDescription,
                onUpdateSetWeight = { setId, weight ->
                    onUpdateSetWeight(
                        exercise.id,
                        setId,
                        weight
                    )
                },
                onUpdateSetReps = { setId, reps -> onUpdateSetReps(exercise.id, setId, reps) },
                onDeleteSet = { setId -> onDeleteSet(exercise.id, setId) }
            )
        }

        Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.small))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddSet(exercise.id) }
                .padding(vertical = ScribbleFitTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.extraSmall)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = addSetLabel.uppercase(),
                style = ScribbleFitTheme.typography.labelMedium,
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
