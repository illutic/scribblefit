package com.scribblefit.core.model

data class Set(
    val id: Long,
    val setNumber: Int,
    val weight: Float,
    val reps: Int,
    val rpe: Float? = null,
    val notes: String? = null
)
