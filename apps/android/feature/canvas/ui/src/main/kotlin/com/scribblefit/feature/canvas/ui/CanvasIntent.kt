package com.scribblefit.feature.canvas.ui

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.navigation.Screen

sealed interface CanvasIntent {
    // Canvas
    data class UpdateScribbleText(
        val text: String,
    ) : CanvasIntent

    data class AddScribble(
        val scribble: String,
    ) : CanvasIntent

    data class RetryScribbleParsing(
        val scribble: Scribble,
    ) : CanvasIntent

    data class ClickOnScribble(
        val scribble: Scribble,
    ) : CanvasIntent

    data object OnPreviousDayClick : CanvasIntent

    data object OnNextDayClick : CanvasIntent

    data object ShowDatePicker : CanvasIntent

    data object DismissDatePicker : CanvasIntent

    data class OnDateSelected(val date: java.time.LocalDate) : CanvasIntent

    // Scribble Dialog
    data class ConfirmScribble(
        val scribble: Scribble,
    ) : CanvasIntent

    data class DeleteScribble(
        val scribbleId: Long,
    ) : CanvasIntent

    data class ShowDeleteConfirmation(val scribbleId: Long) : CanvasIntent
    data object HideDeleteConfirmation : CanvasIntent

    data object DismissScribbleDialog : CanvasIntent

    // Manual Editing
    data class UpdateExerciseName(val scribbleId: Long, val exerciseId: Long, val newName: String) : CanvasIntent
    data class UpdateSetWeight(val exerciseId: Long, val setId: Long, val newWeight: String) :
        CanvasIntent

    data class UpdateSetReps(val exerciseId: Long, val setId: Long, val newReps: String) :
        CanvasIntent

    data class DeleteSet(val exerciseId: Long, val setId: Long) : CanvasIntent
    data class DeleteExercise(val exerciseId: Long) : CanvasIntent
    data class AddSet(val exerciseId: Long) : CanvasIntent

    // Manual Entry
    data object ShowAddExerciseSheet : CanvasIntent
    data object HideAddExerciseSheet : CanvasIntent
    data class SaveManualExercise(
        val name: String,
        val muscleGroup: String,
        val sets: List<com.scribblefit.core.model.Set>,
        val notes: String
    ) : CanvasIntent

    // Navigation
    data object NavigateBack : CanvasIntent

    data class NavigateToScreen(
        val screen: Screen,
    ) : CanvasIntent

    data class NavigateToExerciseDetails(
        val exerciseId: Long,
    ) : CanvasIntent

    data class NavigateToWorkoutExercises(
        val date: Long,
    ) : CanvasIntent
}
