package com.scribblefit.feature.ai.data.engine

import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.ExercisePerformanceInsight
import com.scribblefit.feature.ai.data.entity.AIInsightDto
import com.scribblefit.feature.ai.data.entity.WorkoutDto
import com.scribblefit.feature.ai.data.entity.toDomain
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.ParsingStatus
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

internal class LocalAIEngine(
    private val generativeModel: GenerativeModel,
    private val json: Json,
    private val configRepository: ConfigRepository
) : LLMEngine {
    private val logger = LoggerFactory.getLogger(LocalAIEngine::class.java)
    private val config get() = configRepository.config.value

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> = runCatching {
        val startMs = System.currentTimeMillis()
        ensureModelIsReady()
        val prompt = config.parsePrompt.replace("{{rawText}}", rawText)
        val responseText = callLocalLLM(prompt)
        val workout = json.decodeFromString<WorkoutDto>(responseText)

        ParsedWorkoutResult(
            workout = workout.toDomain(),
            rawText = rawText,
            status = ParsingStatus.SUCCESS,
            parsedJson = responseText,
            modelUsed = "gemini-nano",
            processingTimeMs = System.currentTimeMillis() - startMs
        )
    }

    override suspend fun generateInsightsSummary(exercises: List<Exercise>): Result<List<AIInsight>> =
        runCatching {
            ensureModelIsReady()

            val context = exercises.joinToString("\n") { exercise ->
                val sets = exercise.sets.joinToString(", ") { "${it.weight}x${it.reps}" }
                "${exercise.canonicalName} (${exercise.muscleGroup}): $sets"
            }

            val prompt = config.summaryPrompt.replace("{{workoutData}}", context)
            val responseText = callLocalLLM(prompt)
            val dtos = json.decodeFromString<List<AIInsightDto>>(responseText)
            dtos.map { it.toDomain() }
        }

    override suspend fun generateExerciseInsight(history: String): Result<ExercisePerformanceInsight> {
        return Result.failure(NotImplementedError("Local LLM insight generation not yet implemented"))
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
