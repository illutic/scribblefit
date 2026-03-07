package com.scribblefit.feature.ai.domain.engine

import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult

interface LLMEngine {
    suspend fun parseWorkout(rawText: String): ParsedWorkoutResult
}
