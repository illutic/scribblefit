package com.scribblefit.core.model

/**
 * Domain model representing a scribble.
 */
data class Scribble(
    val id: Long,
    val rawText: String,
    val parsedJson: String?,
    val status: String, // String representation for domain
    val workoutExerciseId: Long?,
    val createdAt: Long
)
