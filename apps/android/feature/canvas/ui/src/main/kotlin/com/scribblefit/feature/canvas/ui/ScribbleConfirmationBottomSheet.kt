package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScribbleConfirmationBottomSheet(
    scribble: Scribble,
    weightUnit: Weight,
    onConfirm: (Scribble) -> Unit,
    onEdit: (Scribble) -> Unit,
    onDelete: (Scribble) -> Unit,
    onDismiss: () -> Unit,
    onUpdateExerciseName: (Long, String) -> Unit,
    onUpdateSetWeight: (Long, Long, String) -> Unit,
    onUpdateSetReps: (Long, Long, String) -> Unit,
    onDeleteSet: (Long, Long) -> Unit,
) {
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
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.canvas_dialog_title),
                style = ScribbleFitTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ScribbleFitTheme.colors.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                scribble.exercises.forEach { exercise ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                ScribbleFitTheme.colors.surfaceContainerLow,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
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
                        Spacer(modifier = Modifier.height(12.dp))
                        exercise.sets.forEach { set ->
                            val weightDisplay = if (weightUnit == Weight.KGS) {
                                stringResource(R.string.canvas_weight_unit_kg)
                            } else {
                                stringResource(R.string.canvas_weight_unit_lb)
                            }
                            var weightText by remember(set.id, set.weight) {
                                mutableStateOf(set.weight.toString())
                            }
                            var repsText by remember(set.id, set.reps) {
                                mutableStateOf(set.reps.toString())
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Set ${set.setNumber}: ",
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
                                    text = weightDisplay,
                                    style = ScribbleFitTheme.typography.bodyMedium,
                                    color = ScribbleFitTheme.colors.midGray
                                )
                                Text(
                                    text = " x ",
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
                                    text = "reps",
                                    style = ScribbleFitTheme.typography.bodyMedium,
                                    color = ScribbleFitTheme.colors.midGray
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { onDeleteSet(exercise.id, set.id) }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Remove set",
                                        tint = ScribbleFitTheme.colors.dangerRed,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { onConfirm(scribble) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { onEdit(scribble) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, ScribbleFitTheme.colors.surfaceContainerHigh)
                ) {
                    Text(
                        text = stringResource(R.string.canvas_dialog_edit),
                        style = ScribbleFitTheme.typography.bodyMedium,
                        color = ScribbleFitTheme.colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { onDelete(scribble) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
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
