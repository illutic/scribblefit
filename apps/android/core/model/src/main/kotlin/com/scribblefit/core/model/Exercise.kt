package com.scribblefit.core.model

data class Exercise(
    val id: Long,
    val canonicalName: String,
    val muscleGroup: String,
    val sets: List<Set>,
    val isDraft: Boolean = false,
    val estimated1RM: Float? = null,
    val intensity: Float? = null,
    val improvement: Float? = null,
)
