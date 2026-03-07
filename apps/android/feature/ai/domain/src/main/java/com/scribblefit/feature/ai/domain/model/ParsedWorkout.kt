package com.scribblefit.feature.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ParsedWorkout(
    val date: String,
    val location: String? = null,
    val exercises: List<ParsedExercise>
)

@Serializable
data class ParsedExercise(
    val canonicalName: String,
    val sets: List<ParsedSet>
)

@Serializable
data class ParsedSet(
    val weight: Double,
    val reps: Int,
    val rpe: Double? = null,
    val notes: String? = null
)
