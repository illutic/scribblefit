package com.scribblefit.feature.exercises.ui

sealed class ExerciseDetailsIntent {
    data class LoadDetails(val exerciseId: Long) : ExerciseDetailsIntent()
    object RefreshAIInsight : ExerciseDetailsIntent()
    object NavigateBack : ExerciseDetailsIntent()
    object NavigateToTrends : ExerciseDetailsIntent()
    object NavigateToHistory : ExerciseDetailsIntent()
    object RemoveExercise : ExerciseDetailsIntent()
}
