package com.scribblefit.feature.ledger.domain.model

data class WorkoutHistory(
    val id: String,
    val date: Long,
    val location: String?,
    val totalVolume: Double,
    val exercises: List<ExerciseHistory>
)

data class ExerciseHistory(
    val canonicalName: String,
    val sets: List<SetHistory>
)

data class SetHistory(
    val weight: Double,
    val reps: Int,
    val rpe: Double?,
    val notes: String?
)
