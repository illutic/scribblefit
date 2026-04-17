package com.scribblefit.feature.ai.domain

import com.scribblefit.core.model.Workout

data class ParsedWorkoutResult(
    val workout: Workout,
    val rawText: String,
    val status: ParsingStatus,
    val parsedJson: String? = null,
    val modelUsed: String? = null,
    val processingTimeMs: Long = 0,
    val reasoning: String? = null,
    val error: String? = null
)

enum class ParsingStatus { SUCCESS, PARTIAL_SUCCESS, FAILURE }
