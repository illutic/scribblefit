package com.scribblefit.core.database.dao

/**
 * Lightweight data holder for per-exercise calculated stats passed into
 * [WorkoutDao.insertWorkoutWithDetails].
 */
data class ExerciseStats(
    val estimated1RM: Float? = null,
    val intensity: Float? = null,
    val improvement: Float? = null,
)
