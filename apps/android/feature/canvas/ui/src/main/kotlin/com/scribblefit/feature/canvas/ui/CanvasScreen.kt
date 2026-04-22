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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.components.ScribbleFitDatePickerDialog
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.ui.components.CanvasBody
import com.scribblefit.feature.canvas.ui.components.CanvasFooter
import com.scribblefit.feature.canvas.ui.components.CanvasTopBar
import com.scribblefit.feature.canvas.ui.components.ScribbleConfirmationBottomSheet

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CanvasScreen(
    state: CanvasState,
    onIntent: (CanvasIntent) -> Unit,
) {
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
                    onScribbleClick = { onIntent(CanvasIntent.ClickOnScribble(it)) },
                    onExerciseClick = { onIntent(CanvasIntent.NavigateToExerciseDetails(it)) },
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
            onUpdateExerciseName = { id, name ->
                onIntent(
                    CanvasIntent.UpdateExerciseName(
                        id,
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
            onDeleteSet = { exId, setId -> onIntent(CanvasIntent.DeleteSet(exId, setId)) }
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
}
