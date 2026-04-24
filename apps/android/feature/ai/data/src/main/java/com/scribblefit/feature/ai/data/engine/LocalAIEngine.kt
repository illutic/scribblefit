package com.scribblefit.feature.ai.data.engine

import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.ai.data.entity.AIInsightDto
import com.scribblefit.feature.ai.data.entity.ExerciseDto
import com.scribblefit.feature.ai.data.entity.toDomain
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult
import kotlinx.serialization.json.Json

internal class LocalAIEngine(
    private val generativeModel: GenerativeModel,
    private val json: Json,
    private val configRepository: ConfigRepository
) : LLMEngine {
    private val config get() = configRepository.config.value

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> = runCatching {
        ensureModelIsReady()
        val prompt = config.remoteConfig.parsePrompt.replace("{{rawText}}", rawText)
        val responseText = callLocalLLM(prompt)
        val exercises = json.decodeFromString<List<ExerciseDto>>(responseText)
        ParsedWorkoutResult(
            rawText = rawText,
            exercises = exercises.map { it.toDomain() }
        )
    }

    override suspend fun generateInsightsSummary(exercises: List<Exercise>): Result<List<AIInsight>> =
        runCatching {
            ensureModelIsReady()

            val context = exercises.joinToString("\n") { exercise ->
                val sets = exercise.sets.joinToString(", ") { "${it.weight}x${it.reps}" }
                "${exercise.canonicalName} (${exercise.muscleGroup}): $sets"
            }

            val prompt = config.remoteConfig.summaryPrompt.replace("{{workoutData}}", context)
            val responseText = callLocalLLM(prompt)
            val dtos = json.decodeFromString<List<AIInsightDto>>(responseText)
            dtos.map { it.toDomain() }
        }

    override suspend fun generateExerciseInsight(history: String): Result<AIInsight> {
        return runCatching {
            ensureModelIsReady()
            val prompt = config.remoteConfig.insightPrompt.replace("{{exerciseHistory}}", history)
            val responseText = callLocalLLM(prompt)
            val dto = json.decodeFromString<AIInsightDto>(responseText)
            dto.toDomain()
        }
    }

    override suspend fun isSupported(): Boolean {
        return generativeModel.checkStatus() == FeatureStatus.AVAILABLE
    }

    private suspend fun callLocalLLM(prompt: String): String {
        val response = generativeModel.generateContent(prompt).candidates.firstOrNull()
        val responseText = response?.text ?: error("No response from LLM")
        return responseText.replaceFirst("```json", "").replaceFirst("```", "").trim()
    }

    private suspend fun ensureModelIsReady() {
        if (generativeModel.checkStatus() != FeatureStatus.AVAILABLE) {
            error("Gemini Nano is not downloaded or ready. Current status: ${generativeModel.checkStatus()}")
        }
    }
}
