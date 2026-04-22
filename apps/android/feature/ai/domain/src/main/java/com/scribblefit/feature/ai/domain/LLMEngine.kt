package com.scribblefit.feature.ai.domain

import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.ExercisePerformanceInsight
import javax.inject.Qualifier

interface LLMEngine {
    suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult>
    suspend fun generateInsightsSummary(exercises: List<Exercise>): Result<List<AIInsight>>
    suspend fun generateExerciseInsight(history: String): Result<ExercisePerformanceInsight>
    suspend fun isSupported(): Boolean
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CloudEngine

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalEngine
