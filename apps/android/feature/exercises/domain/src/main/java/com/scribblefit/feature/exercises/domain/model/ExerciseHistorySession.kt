package com.scribblefit.feature.exercises.domain.model

import com.scribblefit.core.model.Exercise

/**
 * Domain model representing a single historical session for an exercise.
 */
data class ExerciseHistorySession(
    val exercise: Exercise,
    val totalVolume: Float,
    val maxWeight: Float,
    val summary: String,
    val isPersonalBest: Boolean,
    val scribbleId: Long
)
