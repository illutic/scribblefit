package com.scribblefit.feature.exercises.ui

sealed interface WorkoutExercisesIntent {
    data class ExerciseClicked(val exerciseName: String) : WorkoutExercisesIntent
    data object NavigateBack : WorkoutExercisesIntent
}
