package com.scribblefit.core.model

data class Exercise(
    val id: Long,
    val canonicalName: String,
    val muscleGroup: String,
    val sets: List<Set>
)