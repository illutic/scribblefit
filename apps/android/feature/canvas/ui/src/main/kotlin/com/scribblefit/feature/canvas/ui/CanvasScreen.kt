package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.navigation.Screen

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
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                CanvasBody(
                    scribbles = state.scribbles,
                    aiInsights = state.aiInsights,
                    isGeneratingInsights = state.isGeneratingInsights,
                    weightUnit = state.weightUnit,
                    onScribbleClick = { onIntent(CanvasIntent.ClickOnScribble(it)) },
                    onIntent = onIntent,
                    emptyText = state.emptyScribbleText,
                    aiInsightsLabel = state.aiInsightsLabel,
                    closeContentDescription = state.closeContentDescription,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (state.isInputExpanded) {
                                Modifier.imeNestedScroll()
                            } else {
                                Modifier
                            }
                        )
                )

                CanvasFooter(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .navigationBarsPadding()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }

    state.selectedScribble?.let { scribble ->
        ScribbleConfirmationBottomSheet(
            scribble = scribble,
            weightUnit = state.weightUnit,
            onConfirm = { onIntent(CanvasIntent.ConfirmScribble(it)) },
            onEdit = { onIntent(CanvasIntent.UpdateScribble(it)) },
            onDelete = { onIntent(CanvasIntent.DeleteScribble(it.id)) },
            onDismiss = { onIntent(CanvasIntent.DismissScribbleDialog) },
            onUpdateExerciseName = { id, name -> onIntent(CanvasIntent.UpdateExerciseName(id, name)) },
            onUpdateSetWeight = { exId, setId, weight -> onIntent(CanvasIntent.UpdateSetWeight(exId, setId, weight)) },
            onUpdateSetReps = { exId, setId, reps -> onIntent(CanvasIntent.UpdateSetReps(exId, setId, reps)) },
            onDeleteSet = { exId, setId -> onIntent(CanvasIntent.DeleteSet(exId, setId)) }
        )
    }

    if (state.isDatePickerVisible) {
        CanvasDatePickerDialog(
            initialDate = state.currentDate,
            onDateSelected = { onIntent(CanvasIntent.OnDateSelected(it)) },
            onDismiss = { onIntent(CanvasIntent.DismissDatePicker) }
        )
    }
}
