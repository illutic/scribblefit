package com.scribblefit.api.features.exercises

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDto(
    val id: String,
    val canonicalName: String,
    val muscleGroup: String,
    val aliases: List<String>
)

@Serializable
data class ExerciseResponse(
    val exercises: List<ExerciseDto>
)
