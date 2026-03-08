package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.model.ParsingStatus

class LocalAIEngine : LLMEngine {
    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult =
        ParsedWorkoutResult(
            workout = null,
            rawText = rawText,
            status = ParsingStatus.FAILURE,
            error = "Local AI not yet available"
        )
}
