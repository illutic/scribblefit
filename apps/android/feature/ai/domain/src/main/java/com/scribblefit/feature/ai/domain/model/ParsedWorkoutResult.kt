package com.scribblefit.feature.ai.domain.model

data class ParsedWorkoutResult(
    val workout: ParsedWorkout?,
    val rawText: String,
    val status: ParsingStatus,
    val modelUsed: String? = null,
    val processingTimeMs: Long = 0,
    val reasoning: String? = null,
    val error: String? = null
)

enum class ParsingStatus { SUCCESS, PARTIAL_SUCCESS, FAILURE }
