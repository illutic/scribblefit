package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AIParsingException
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParseRequest
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import org.slf4j.LoggerFactory
import javax.inject.Inject

class ScribbleFitProxyEngine @Inject constructor(
    private val api: ScribbleFitApi,
    private val secureKeyStorage: SecureKeyStorage,
    private val systemPrompt: String
) : LLMEngine {

    private val logger = LoggerFactory.getLogger(javaClass)
    
    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> = runCatching {
        val token = secureKeyStorage.getAuthToken()
        
        val request = ParseRequest(
            rawText = rawText,
            prompt = systemPrompt
        )
        
        try {
            val responseDto = api.parseProxy(request, token)
            responseDto.toDomain()
        } catch (e: Exception) {
            // Check if it's a decoding error or server error
            // ScribbleFitApi already handles some level of this, but we wrap it here
            logger.error("Proxy parsing failed for: $rawText", e)
            throw AIParsingException(rawText = rawText, error = "Proxy Failure: ${e.message}", cause = e)
        }
    }
}
