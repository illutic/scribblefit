package com.scribblefit.api.features.parser

import kotlinx.serialization.Serializable

@Serializable
data class ParsedWorkoutDto(
    val date: String,
    val location: String? = null,
    val exercises: List<ParsedExerciseDto>
)

@Serializable
data class ParsedExerciseDto(
    val canonicalName: String,
    val sets: List<ParsedSetDto>
)

@Serializable
data class ParsedSetDto(
    val weight: Double,
    val reps: Int,
    val rpe: Double? = null,
    val notes: String? = null
)
