package com.scribblefit.feature.exercises.ui

sealed class ExerciseDetailsIntent {
    data class LoadDetails(val exerciseName: String) : ExerciseDetailsIntent()
    object RefreshAIInsight : ExerciseDetailsIntent()
    object NavigateBack : ExerciseDetailsIntent()
}
