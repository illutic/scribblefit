package com.scribblefit.feature.ai.data.engine

import com.google.firebase.Firebase
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.ai.data.entity.AIInsightDto
import com.scribblefit.feature.ai.data.entity.WorkoutDto
import com.scribblefit.feature.ai.data.entity.toDomain
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.ParsingStatus
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
        val prompt = config.parsePrompt.replace("{{rawText}}", rawText)
        val startMs = System.currentTimeMillis()
        
        val response = getModel().generateContent(prompt)
        val responseText = response.text ?: error("No response from Gemini")
        val cleanJson = responseText.replaceFirst("```json", "").replaceFirst("```", "").trim()
        
        val workout = json.decodeFromString<WorkoutDto>(cleanJson)

        ParsedWorkoutResult(
            workout = workout.toDomain(),
            rawText = rawText,
            status = ParsingStatus.SUCCESS,
            parsedJson = responseText,
            processingTimeMs = System.currentTimeMillis() - startMs
        )
    }

    override suspend fun generateInsightsSummary(exercises: List<Exercise>): Result<List<AIInsight>> =
        runCatching {
            val context = exercises.joinToString("\n") { exercise ->
                val sets = exercise.sets.joinToString(", ") { "${it.weight}x${it.reps}" }
                "${exercise.canonicalName} (${exercise.muscleGroup}): $sets"
            }

            val prompt = config.summaryPrompt.replace("{{workoutData}}", context)

            val response = getModel().generateContent(prompt)
            val responseText = response.text ?: error("No response from Gemini")
            val cleanJson = responseText.replaceFirst("```json", "").replaceFirst("```", "").trim()

            val dtos = json.decodeFromString<List<AIInsightDto>>(cleanJson)
            dtos.map { it.toDomain() }
        }
}
