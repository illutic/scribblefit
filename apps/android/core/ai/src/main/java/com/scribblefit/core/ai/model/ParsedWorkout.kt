package com.scribblefit.core.ai.model

data class ParsedWorkout(
    val date: String,
    val location: String? = null,
    val exercises: List<ParsedExercise>
)

data class ParsedExercise(
    val canonicalName: String,
    val sets: List<ParsedSet>
)

data class ParsedSet(
    val weight: Double,
    val reps: Int,
    val rpe: Double? = null,
    val notes: String? = null
)
