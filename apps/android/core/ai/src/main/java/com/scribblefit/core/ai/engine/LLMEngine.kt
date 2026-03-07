package com.scribblefit.core.ai.engine

import com.scribblefit.core.ai.model.ParsedWorkout

interface LLMEngine {
    suspend fun parseWorkout(rawText: String): Result<ParsedWorkout>
}
