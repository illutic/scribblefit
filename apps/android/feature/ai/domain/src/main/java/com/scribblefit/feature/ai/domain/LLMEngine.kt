package com.scribblefit.feature.ai.domain

import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import kotlinx.coroutines.flow.Flow

interface LLMEngine {
    suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult>
    suspend fun generateInsightsSummary(exercises: List<Exercise>): Result<List<AIInsight>>
}

interface CloudLLMEngine : LLMEngine {
    suspend fun validateApiKey(apiKey: String): Result<Unit>
    suspend fun getAvailableModels(apiKey: String): Result<List<String>>
}

interface LocalLLMEngine : LLMEngine {
    suspend fun isSupported(): Boolean
}

interface LLMEngineProxy {
    val underlyingEngine: Flow<LLMEngine>
}