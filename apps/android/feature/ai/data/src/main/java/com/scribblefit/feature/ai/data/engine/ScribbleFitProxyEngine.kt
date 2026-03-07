package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.*
import com.scribblefit.feature.ai.domain.engine.*
import com.scribblefit.feature.ai.domain.model.*
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParseRequest
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ScribbleFitProxyEngine @Inject constructor(
    private val api: ScribbleFitApi,
    private val secureKeyStorage: SecureKeyStorage,
    private val systemPrompt: String
) : LLMEngine, AnalysisEngine {

    private val logger = LoggerFactory.getLogger(javaClass)
    
    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> = runCatching {
        val token = secureKeyStorage.getAuthToken()
        val request = ParseRequest(rawText = rawText, prompt = systemPrompt)
        try {
            api.parseProxy(request, token).toDomain()
        } catch (e: Exception) {
            throw AIParsingException(rawText = rawText, error = "Proxy Failure: ${e.message}", cause = e)
        }
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> = runCatching {
        throw UnsupportedOperationException("Proxy analysis not yet supported by backend schema")
    }

    override suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary> = runCatching {
        throw UnsupportedOperationException("Proxy analysis not yet supported by backend schema")
    }

    override suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight> = runCatching {
        throw UnsupportedOperationException("Proxy analysis not yet supported by backend schema")
    }
}
