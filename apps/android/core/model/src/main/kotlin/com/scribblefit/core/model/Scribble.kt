package com.scribblefit.core.model

/**
 * Domain model representing a scribble.
 */
data class Scribble(
    val id: Long,
    val rawText: String,
    val parsedJson: String? = null,
    val status: ScribbleStatus,
    val createdAt: Long,
    val exercises: List<Exercise> = emptyList(),
)

enum class ScribbleStatus {
    RAW,
    PARSED,
    COMPLETED,
    FAILED
}
