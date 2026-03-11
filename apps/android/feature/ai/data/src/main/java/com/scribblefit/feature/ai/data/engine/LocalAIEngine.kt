package com.scribblefit.feature.ai.data.engine

import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.feature.ai.data.entity.WorkoutDto
import com.scribblefit.feature.ai.data.entity.toDomain
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.model.ParsingStatus
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
        val prompt = "${config.parsePrompt}\n\nInput: $rawText"
        val response = generativeModel.generateContent(prompt).candidates.firstOrNull()
        val responseText = response?.text ?: error("No response from LLM")
        val workout = json.decodeFromString<WorkoutDto>(responseText)

        ParsedWorkoutResult(
            workout = workout.toDomain(),
            rawText = rawText,
            status = ParsingStatus.SUCCESS,
            modelUsed = "gemini-nano",
            processingTimeMs = System.currentTimeMillis() - startMs
        )
    }

    private suspend fun ensureModelIsReady() {
        when (generativeModel.checkStatus()) {
            FeatureStatus.UNAVAILABLE -> {
                error("Gemini Nano is Not available")
            }

            FeatureStatus.DOWNLOADABLE -> {
                var totalBytesToDownload: Long = -1L
                // Gemini Nano can be downloaded on this device, but is not currently downloaded
                generativeModel.download().collect { status ->
                    when (status) {
                        is DownloadStatus.DownloadStarted -> {
                            totalBytesToDownload = status.bytesToDownload
                            logger.info("Gemini Nano download started $totalBytesToDownload bytes")
                        }

                        is DownloadStatus.DownloadProgress -> {
                            val progress = (status.totalBytesDownloaded / totalBytesToDownload) * 100
                            logger.info("Gemini Nano download progress: $progress%")
                        }

                        DownloadStatus.DownloadCompleted -> {
                            logger.info("Gemini Nano download completed")
                        }

                        is DownloadStatus.DownloadFailed -> {
                            logger.error("Gemini Nano download failed: ${status.e}")
                        }
                    }
                }
            }

            FeatureStatus.DOWNLOADING -> {
                // Gemini Nano is currently being downloaded on this device
                logger.info("Gemini Nano is currently downloading")
            }

            FeatureStatus.AVAILABLE -> {
                // Gemini Nano is downloaded and ready to use
                logger.info("Gemini Nano is available and ready to use")
            }
        }
    }
}
