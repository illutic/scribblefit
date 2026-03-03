package com.scribblefit.core.domain.engine

import com.scribblefit.core.model.ParsedWorkout

interface LLMEngine {
    suspend fun parseWorkout(rawText: String, prompt: String): Result<ParsedWorkout>
}
