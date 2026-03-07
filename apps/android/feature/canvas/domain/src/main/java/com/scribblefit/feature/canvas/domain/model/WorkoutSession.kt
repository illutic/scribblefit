package com.scribblefit.feature.canvas.domain.model

/**
 * Represents a transient workout session before it's committed to the ledger.
 */
data class WorkoutSession(
    val id: String,
    val startTime: Long,
    val lastActivityTime: Long,
    val exercises: List<SessionExercise>
)

data class SessionExercise(
    val canonicalName: String,
    val sets: List<SessionSet>
)

data class SessionSet(
    val weight: Double,
    val reps: Int,
    val rpe: Double?,
    val notes: String?
)
