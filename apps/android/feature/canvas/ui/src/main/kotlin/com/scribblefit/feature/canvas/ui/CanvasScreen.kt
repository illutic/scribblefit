package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.components.ScribbleFitDatePickerDialog
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.ui.components.CanvasBody
import com.scribblefit.feature.canvas.ui.components.CanvasFooter
import com.scribblefit.feature.canvas.ui.components.CanvasTopBar
import com.scribblefit.feature.canvas.ui.components.ScribbleConfirmationBottomSheet
import com.scribblefit.feature.exercises.ui.components.edit.AddExerciseBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun CanvasScreen(
    state: CanvasState,
    onIntent: (CanvasIntent) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            onIntent(CanvasIntent.DismissError)
            scope.launch {
                snackbarHostState.showSnackbar(error.message ?: "An error occurred")
            }
        }
    }

    Scaffold(
        topBar = {
            CanvasTopBar(
                dateString = state.dateString,
                onPreviousDayClick = { onIntent(CanvasIntent.OnPreviousDayClick) },
                onNextDayClick = { onIntent(CanvasIntent.OnNextDayClick) },
                onDateClick = { onIntent(CanvasIntent.ShowDatePicker) },
                onSettingsClick = { onIntent(CanvasIntent.NavigateToScreen(Screen.Settings)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ScribbleFitTheme.colors.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
                .imePadding()
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                CanvasBody(
                    state = state,
                    onIntent = onIntent,
                )

                CanvasFooter(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }

    if (state.selectedScribble != null) {
        ScribbleConfirmationBottomSheet(
            state = state,
            onConfirm = { onIntent(CanvasIntent.ConfirmScribble(it)) },
            onDelete = { onIntent(CanvasIntent.ShowDeleteConfirmation(it.id)) },
            onDismiss = { onIntent(CanvasIntent.DismissScribbleDialog) },
            onUpdateExerciseName = { scId, exId, name ->
                onIntent(
                    CanvasIntent.UpdateExerciseName(
                        scId,
                        exId,
                        name
                    )
                )
            },
            onUpdateSetWeight = { exId, setId, weight ->
                onIntent(
                    CanvasIntent.UpdateSetWeight(
                        exId,
                        setId,
                        weight
                    )
                )
            },
            onUpdateSetReps = { exId, setId, reps ->
                onIntent(
                    CanvasIntent.UpdateSetReps(
                        exId,
                        setId,
                        reps
                    )
                )
            },
            onDeleteSet = { exId, setId -> onIntent(CanvasIntent.DeleteSet(exId, setId)) },
            onDeleteExercise = { onIntent(CanvasIntent.DeleteExercise(it)) },
            onAddSet = { onIntent(CanvasIntent.AddSet(it)) },
        )
    }

    if (state.isDatePickerVisible) {
        ScribbleFitDatePickerDialog(
            initialDate = state.currentDate,
            onDateSelected = { onIntent(CanvasIntent.OnDateSelected(it)) },
            onDismiss = { onIntent(CanvasIntent.DismissDatePicker) }
        )
    }

    if (state.showDeleteConfirmation && state.deletingScribbleId != null) {
        AlertDialog(
            onDismissRequest = { onIntent(CanvasIntent.HideDeleteConfirmation) },
            title = { Text(text = state.deleteDialogTitle) },
            text = { Text(text = state.deleteDialogText) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onIntent(CanvasIntent.DeleteScribble(state.deletingScribbleId))
                    }
                ) {
                    Text(state.deleteConfirmLabel, color = ScribbleFitTheme.colors.dangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(CanvasIntent.HideDeleteConfirmation) }) {
                    Text(state.deleteCancelLabel)
                }
            },
            containerColor = ScribbleFitTheme.colors.surface
        )
    }

    if (state.isAddExerciseSheetVisible) {
        AddExerciseBottomSheet(
            sheetState = sheetState,
            weightUnitLabel = state.weightUnitLabel,
            onDismiss = { onIntent(CanvasIntent.HideAddExerciseSheet) },
            onSave = { name, muscle, sets ->
                onIntent(CanvasIntent.SaveManualExercise(name, muscle, sets))
            }
        )
    }
}
