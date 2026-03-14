package com.scribblefit.feature.ai.domain

interface LLMEngine {
    suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult>
}