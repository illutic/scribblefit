package com.scribblefit.feature.exercises.ui.history

sealed interface ExerciseHistoryIntent {
    data class LoadHistory(val exerciseName: String) : ExerciseHistoryIntent
    data class NavigateToScribble(val scribbleId: Long, val sessionDate: Long) : ExerciseHistoryIntent
    data object NavigateBack : ExerciseHistoryIntent
}
