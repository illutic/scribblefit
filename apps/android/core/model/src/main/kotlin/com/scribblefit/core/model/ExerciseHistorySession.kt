package com.scribblefit.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseHistorySession(
    val workoutId: Long,
    val date: Long,
    val exercise: Exercise
)
