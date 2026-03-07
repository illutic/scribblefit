package com.scribblefit.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParsedWorkoutDto(
    val date: String,
    val location: String? = null,
    val exercises: List<ParsedExerciseDto>
)

@Serializable
data class ParsedExerciseDto(
    @SerialName("canonical_name")
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
