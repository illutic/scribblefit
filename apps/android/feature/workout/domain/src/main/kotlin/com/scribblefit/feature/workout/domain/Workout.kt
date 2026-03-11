package com.scribblefit.feature.workout.domain

data class Workout(
    val date: String,
    val exercises: List<Exercise>
)

data class Exercise(
    val canonicalName: String,
    val muscleGroup: String,
    val sets: List<Set>
)

data class Set(
    val weight: Double,
    val reps: Int,
    val rpe: Double? = null,
    val notes: String? = null
)
