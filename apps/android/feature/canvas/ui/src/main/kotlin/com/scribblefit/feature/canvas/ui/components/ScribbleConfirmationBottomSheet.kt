package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.canvas.ui.CanvasState
import com.scribblefit.feature.canvas.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScribbleConfirmationBottomSheet(
    state: CanvasState,
    onConfirm: (Scribble) -> Unit,
    onDelete: (Scribble) -> Unit,
    onDismiss: () -> Unit,
    onUpdateExerciseName: (Long, String) -> Unit,
    onUpdateSetWeight: (Long, Long, String) -> Unit,
    onUpdateSetReps: (Long, Long, String) -> Unit,
    onDeleteSet: (Long, Long) -> Unit,
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
                        Column(
                            modifier = Modifier
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
                                var weightText by remember(set.id, set.weight) {
                                    mutableStateOf(set.weight.toString())
                                }
                                var repsText by remember(set.id, set.reps) {
                                    mutableStateOf(set.reps.toString())
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.setLabelFormat.format(set.setNumber),
                                        style = ScribbleFitTheme.typography.bodyMedium,
                                        color = ScribbleFitTheme.colors.midGray
                                    )
                                    BasicTextField(
                                        value = weightText,
                                        onValueChange = { newValue ->
                                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                                weightText = newValue
                                                newValue.toFloatOrNull()?.let {
                                                    onUpdateSetWeight(exercise.id, set.id, newValue)
                                                }
                                            }
                                        },
                                        modifier = Modifier.width(64.dp),
                                        textStyle = ScribbleFitTheme.typography.bodyMedium.copy(
                                            color = ScribbleFitTheme.colors.primary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        cursorBrush = SolidColor(ScribbleFitTheme.colors.primary)
                                    )
                                    Text(
                                        text = state.weightUnitLabel,
                                        style = ScribbleFitTheme.typography.bodyMedium,
                                        color = ScribbleFitTheme.colors.midGray
                                    )
                                    Text(
                                        text = state.setRepsSeparator,
                                        style = ScribbleFitTheme.typography.bodyMedium,
                                        color = ScribbleFitTheme.colors.midGray
                                    )
                                    BasicTextField(
                                        value = repsText,
                                        onValueChange = { newValue ->
                                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                repsText = newValue
                                                newValue.toIntOrNull()?.let {
                                                    onUpdateSetReps(exercise.id, set.id, newValue)
                                                }
                                            }
                                        },
                                        modifier = Modifier.width(48.dp),
                                        textStyle = ScribbleFitTheme.typography.bodyMedium.copy(
                                            color = ScribbleFitTheme.colors.primary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        cursorBrush = SolidColor(ScribbleFitTheme.colors.primary)
                                    )
                                    Text(
                                        text = state.repsLabel,
                                        style = ScribbleFitTheme.typography.bodyMedium,
                                        color = ScribbleFitTheme.colors.midGray
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(onClick = { onDeleteSet(exercise.id, set.id) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = state.deleteSetContentDescription,
                                            tint = ScribbleFitTheme.colors.dangerRed,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                }
                            }
                        }
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
