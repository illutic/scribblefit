package com.scribblefit.feature.exercises.ui.components.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseBottomSheet(
    sheetState: SheetState,
    weightUnitLabel: String,
    onDismiss: () -> Unit,
    onSave: (exerciseName: String, muscleGroup: String, sets: List<Set>, notes: String) -> Unit,
) {
    var exerciseName by remember { mutableStateOf("") }
    var muscleGroup by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var sets by remember {
        mutableStateOf(
            listOf(
                Set(id = 1, setNumber = 1, weight = 0f, reps = 0),
                Set(id = 2, setNumber = 2, weight = 0f, reps = 0)
            )
        )
    }

    val isSaveEnabled = exerciseName.isNotBlank() &&
            muscleGroup.isNotBlank() &&
            sets.any { (it.weight ?: 0f) > 0f && it.reps > 0 }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ScribbleFitTheme.colors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .background(
                        ScribbleFitTheme.colors.surfaceContainerHigh,
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.add_exercise_close),
                        tint = ScribbleFitTheme.colors.primary
                    )
                }
                Text(
                    text = stringResource(R.string.add_exercise_title),
                    style = ScribbleFitTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.primary
                )
                TextButton(
                    onClick = { onSave(exerciseName, muscleGroup, sets, notes) },
                    enabled = isSaveEnabled
                ) {
                    Text(
                        text = stringResource(R.string.add_exercise_save),
                        style = ScribbleFitTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isSaveEnabled) ScribbleFitTheme.colors.primary else ScribbleFitTheme.colors.midGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Content
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = stringResource(R.string.add_exercise_label_exercise),
                            style = ScribbleFitTheme.typography.labelSmall,
                            color = ScribbleFitTheme.colors.midGray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AddExerciseInput(
                            value = exerciseName,
                            onValueChange = { exerciseName = it },
                            placeholder = stringResource(R.string.add_exercise_placeholder_name)
                        )
                    }
                }

                item {
                    Column {
                        Text(
                            text = stringResource(R.string.add_exercise_label_muscle),
                            style = ScribbleFitTheme.typography.labelSmall,
                            color = ScribbleFitTheme.colors.midGray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AddExerciseInput(
                            value = muscleGroup,
                            onValueChange = { muscleGroup = it },
                            placeholder = stringResource(R.string.add_exercise_placeholder_muscle)
                        )
                    }
                }

                item {
                    Column {
                        Text(
                            text = stringResource(R.string.add_exercise_label_sets),
                            style = ScribbleFitTheme.typography.labelSmall,
                            color = ScribbleFitTheme.colors.midGray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        sets.forEachIndexed { index, set ->
                            AddExerciseSetRow(
                                setNumber = set.setNumber,
                                weight = set.weight,
                                reps = set.reps,
                                weightUnitLabel = weightUnitLabel,
                                isDeleteVisible = sets.size > 1,
                                onWeightChange = { newWeight ->
                                    sets = sets.toMutableList().apply {
                                        this[index] =
                                            set.copy(weight = newWeight.toFloatOrNull() ?: 0f)
                                    }
                                },
                                onRepsChange = { newReps ->
                                    sets = sets.toMutableList().apply {
                                        this[index] = set.copy(reps = newReps.toIntOrNull() ?: 0)
                                    }
                                },
                                onDelete = {
                                    sets = sets.toMutableList().apply { removeAt(index) }
                                }
                            )
                            if (index < sets.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .clickable {
                                    sets = sets + Set(
                                        id = (sets.maxOfOrNull { it.id } ?: 0L) + 1,
                                        setNumber = sets.size + 1,
                                        weight = 0f,
                                        reps = 0
                                    )
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                tint = ScribbleFitTheme.colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = stringResource(R.string.add_exercise_button_add_set).uppercase(),
                                style = ScribbleFitTheme.typography.labelMedium,
                                color = ScribbleFitTheme.colors.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            text = stringResource(R.string.add_exercise_label_notes),
                            style = ScribbleFitTheme.typography.labelSmall,
                            color = ScribbleFitTheme.colors.midGray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AddExerciseInput(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = stringResource(R.string.add_exercise_placeholder_notes),
                            singleLine = false,
                            minLines = 3
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddExerciseInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                ScribbleFitTheme.colors.surfaceContainerLow,
                RoundedCornerShape(ScribbleFitTheme.shapes.small)
            )
            .padding(16.dp),
        textStyle = ScribbleFitTheme.typography.bodyMedium.copy(
            color = ScribbleFitTheme.colors.primary
        ),
        cursorBrush = SolidColor(ScribbleFitTheme.colors.primary),
        singleLine = singleLine,
        minLines = minLines,
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.5f)
                )
            }
            innerTextField()
        }
    )
}

@Composable
private fun AddExerciseSetRow(
    setNumber: Int,
    weight: Float?,
    reps: Int,
    weightUnitLabel: String,
    isDeleteVisible: Boolean,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "$setNumber",
            style = ScribbleFitTheme.typography.bodyMedium,
            color = ScribbleFitTheme.colors.midGray,
            modifier = Modifier.width(16.dp),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    ScribbleFitTheme.colors.surfaceContainerLow,
                    RoundedCornerShape(ScribbleFitTheme.shapes.small)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = if (weight == 0f) "" else weight?.toString() ?: "",
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        onWeightChange(newValue)
                    }
                },
                modifier = Modifier.weight(1f),
                textStyle = ScribbleFitTheme.typography.bodyMedium.copy(
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                cursorBrush = SolidColor(ScribbleFitTheme.colors.primary),
                decorationBox = { innerTextField ->
                    if (weight == 0f || weight == null) {
                        Text(
                            text = weightUnitLabel,
                            style = ScribbleFitTheme.typography.bodyMedium,
                            color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.5f),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = weightUnitLabel,
                style = ScribbleFitTheme.typography.bodySmall,
                color = ScribbleFitTheme.colors.midGray
            )
        }

        Text(
            text = "x",
            style = ScribbleFitTheme.typography.bodyMedium,
            color = ScribbleFitTheme.colors.midGray
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    ScribbleFitTheme.colors.surfaceContainerLow,
                    RoundedCornerShape(ScribbleFitTheme.shapes.small)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = if (reps == 0) "" else reps.toString(),
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        onRepsChange(newValue)
                    }
                },
                modifier = Modifier.weight(1f),
                textStyle = ScribbleFitTheme.typography.bodyMedium.copy(
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(ScribbleFitTheme.colors.primary),
                decorationBox = { innerTextField ->
                    if (reps == 0) {
                        Text(
                            text = stringResource(R.string.add_exercise_placeholder_reps),
                            style = ScribbleFitTheme.typography.bodyMedium,
                            color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.5f),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.add_exercise_label_reps_short),
                style = ScribbleFitTheme.typography.bodySmall,
                color = ScribbleFitTheme.colors.midGray
            )
        }

        if (isDeleteVisible) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.dangerRed.copy(alpha = 0.6f)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}
