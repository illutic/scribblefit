package com.scribblefit.core.model

import java.util.UUID

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
) {
    val date: Long get() = exercise.createdAt
}
