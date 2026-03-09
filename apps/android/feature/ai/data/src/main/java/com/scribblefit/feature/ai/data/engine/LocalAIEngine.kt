package com.scribblefit.feature.ai.data.engine

import android.util.Log
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.model.ParsingStatus
import com.scribblefit.feature.ai.domain.model.SystemConfig
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json

class LocalAIEngine(
    private val generativeModel: GenerativeModel,
    private val json: Json
) : LLMEngine {

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult {
        val startMs = System.currentTimeMillis()
        return try {
            ensureModelIsReady()
            val prompt = "${SystemConfig.defaultPrompt}\n\nInput: $rawText"
            val response = generativeModel.generateContent(prompt).candidates.firstOrNull()
            val responseText = response?.text ?: return ParsedWorkoutResult(
                workout = null,
                rawText = rawText,
                status = ParsingStatus.FAILURE,
                error = "Empty response from Gemini Nano"
            )
            val workout = json.decodeFromString<ParsedWorkout>(responseText)
            ParsedWorkoutResult(
                workout = workout,
                rawText = rawText,
                status = ParsingStatus.SUCCESS,
                modelUsed = "gemini-nano",
                processingTimeMs = System.currentTimeMillis() - startMs
            )
        } catch (e: Exception) {
            ParsedWorkoutResult(
                workout = null,
                rawText = rawText,
                status = ParsingStatus.FAILURE,
                error = e.message,
                processingTimeMs = System.currentTimeMillis() - startMs
            )
        }
    }

    private suspend fun ensureModelIsReady() {
        when (generativeModel.checkStatus()) {
            FeatureStatus.UNAVAILABLE -> {
                error("Gemini Nano is Not available")
            }

            FeatureStatus.DOWNLOADABLE -> {
                // Gemini Nano can be downloaded on this device, but is not currently downloaded
                generativeModel.download().collect { status ->
                    when (status) {
                        is DownloadStatus.DownloadStarted -> {}

                        is DownloadStatus.DownloadProgress -> {}

                        DownloadStatus.DownloadCompleted -> {}

                        is DownloadStatus.DownloadFailed -> {}
                    }
                }
            }

            FeatureStatus.DOWNLOADING -> {
                // Gemini Nano currently being downloaded
            }

            FeatureStatus.AVAILABLE -> {
                // Gemini Nano currently downloaded and available to use on this device
            }
        }
    }
}
