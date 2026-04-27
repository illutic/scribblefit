package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.canvas.ui.CanvasState
import com.scribblefit.feature.canvas.ui.R
import com.scribblefit.feature.exercises.ui.components.edit.ExerciseEditItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScribbleConfirmationBottomSheet(
    state: CanvasState,
    onConfirm: (Scribble) -> Unit,
    onDelete: (Scribble) -> Unit,
    onDismiss: () -> Unit,
    onUpdateExerciseName: (Long, Long, String) -> Unit,
    onUpdateSetWeight: (Long, Long, String) -> Unit,
    onUpdateSetReps: (Long, Long, String) -> Unit,
    onDeleteSet: (Long, Long) -> Unit,
    onDeleteExercise: (Long) -> Unit,
    onAddSet: (Long) -> Unit
) {
    val scribble = state.selectedScribble ?: return
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ScribbleFitTheme.colors.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = ScribbleFitTheme.colors.midGray.copy(
                    alpha = 0.2f
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScribbleFitTheme.spacing.large)
                .padding(bottom = ScribbleFitTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large)
            ) {
                Text(
                    text = stringResource(R.string.canvas_dialog_title),
                    style = ScribbleFitTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.primary
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)) {
                    items(scribble.exercises) { exercise ->
                        ExerciseEditItem(
                            exercise = exercise,
                            onUpdateExerciseName = { exId, name -> 
                                onUpdateExerciseName(scribble.id, exId, name) 
                            },
                            onUpdateSetWeight = onUpdateSetWeight,
                            onUpdateSetReps = onUpdateSetReps,
                            onDeleteSet = onDeleteSet,
                            weightUnitLabel = state.weightUnitLabel,
                            setRepsSeparator = state.setRepsSeparator,
                            repsLabel = state.repsLabel,
                            deleteSetContentDescription = state.deleteSetContentDescription,
                            addSetLabel = state.addSetLabel,
                            onDeleteExercise = onDeleteExercise,
                            onAddSet = onAddSet,
                        )
                    }

                    item {
                        Button(
                            onClick = { onConfirm(scribble) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = ScribbleFitTheme.colors.primary),
                            shape = CircleShape,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.canvas_dialog_confirm),
                                style = ScribbleFitTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ScribbleFitTheme.colors.onPrimary
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = { onDelete(scribble) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ScribbleFitTheme.colors.dangerRed.copy(
                                    alpha = 0.1f
                                )
                            ),
                            shape = CircleShape,
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.canvas_dialog_delete),
                                style = ScribbleFitTheme.typography.bodyMedium,
                                color = ScribbleFitTheme.colors.dangerRed,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
