package com.scribblefit.feature.ai.domain.model

class AIParsingException(
    val rawText: String,
    val error: String,
    cause: Throwable? = null
) : Exception("AI Parsing failed for text: $rawText. Error: $error", cause)
