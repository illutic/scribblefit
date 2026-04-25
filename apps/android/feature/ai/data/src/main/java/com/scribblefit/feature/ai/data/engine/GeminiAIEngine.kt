package com.scribblefit.feature.ai.data.engine

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.generationConfig
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.ai.data.entity.AIInsightDto
import com.scribblefit.feature.ai.data.entity.WorkoutResponseDto
import com.scribblefit.feature.ai.data.entity.toDomain
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult
import kotlinx.serialization.json.Json

internal class GeminiAIEngine(
    private val configRepository: ConfigRepository,
    private val json: Json
) : LLMEngine {
    private val config get() = configRepository.config.value

    private fun getModel() = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
        modelName = "gemini-2.5-flash-lite",
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    override suspend fun isSupported(): Boolean = true

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> = runCatching {
        // Sanitize input to prevent prompt injection by escaping delimiters
        val sanitizedInput = rawText.replace("""[\{\}]""".toRegex(), " ")
        val prompt = config.remoteConfig.parsePrompt.replace(
            "{{rawText}}",
            "<workout_scribble>$sanitizedInput</workout_scribble>"
        )

        val response = getModel().generateContent(prompt)
        val responseText = response.text ?: error("No response from Gemini")
        val cleanJson = responseText.replaceFirst("```json", "").replaceFirst("```", "").trim()

        val workoutResponse = json.decodeFromString<WorkoutResponseDto>(cleanJson)

        ParsedWorkoutResult(
            rawText = rawText,
            exercises = workoutResponse.exercises.map { it.toDomain() }
        )
    }

    override suspend fun generateInsightsSummary(exercises: List<Exercise>): Result<List<AIInsight>> =
        runCatching {
            val context = exercises.joinToString("\n") { exercise ->
                val sets = exercise.sets.joinToString(", ") { "${it.weight}x${it.reps}" }
                "${exercise.canonicalName} (${exercise.muscleGroup}): $sets"
            }

            val sanitizedContext = context.replace("""[\{\}]""".toRegex(), " ")
            val prompt = config.remoteConfig.summaryPrompt.replace(
                "{{workoutData}}",
                "<workout_history>$sanitizedContext</workout_history>"
            )

            val response = getModel().generateContent(prompt)
            val responseText = response.text ?: error("No response from Gemini")
            val cleanJson = responseText.replaceFirst("```json", "").replaceFirst("```", "").trim()

            val dtos = json.decodeFromString<List<AIInsightDto>>(cleanJson)
            dtos.map { it.toDomain() }
        }

    override suspend fun generateExerciseInsight(history: String): Result<AIInsight> =
        runCatching {
            val sanitizedHistory = history.replace("""[\{\}]""".toRegex(), " ")
            val prompt = config.remoteConfig.insightPrompt.replace(
                "{{exerciseHistory}}",
                "<exercise_history>$sanitizedHistory</exercise_history>"
            )

            val response = getModel().generateContent(prompt)
            val responseText = response.text ?: error("No response from Gemini")
            val cleanJson = responseText.replaceFirst("```json", "").replaceFirst("```", "").trim()

            json.decodeFromString<AIInsightDto>(cleanJson).toDomain()
        }
}
