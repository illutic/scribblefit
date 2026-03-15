package com.scribblefit.core.model

data class Workout(
    val id: Long,
    val date: Long,
    val exercises: List<Exercise>,
    val notes: List<String>? = null,
)
