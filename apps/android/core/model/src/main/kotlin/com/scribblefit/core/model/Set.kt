package com.scribblefit.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Set(
    val id: Long,
    val setNumber: Int,
    val reps: Int,
    val weight: Float? = null,
    val rpe: Float? = null,
    val notes: String? = null
)
