package com.scribblefit.feature.ai.domain.engine

import com.scribblefit.feature.ai.domain.model.ParsedWorkout

interface LLMEngine {
    suspend fun parseWorkout(rawText: String): Result<ParsedWorkout>
}
