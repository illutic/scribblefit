package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParseRequest
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import javax.inject.Inject

class ScribbleFitProxyEngine @Inject constructor(
    private val api: ScribbleFitApi,
    private val secureKeyStorage: SecureKeyStorage,
    private val systemPrompt: String
) : LLMEngine {
    
    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> = runCatching {
        val token = secureKeyStorage.getAuthToken()
        
        val request = ParseRequest(
            rawText = rawText,
            prompt = systemPrompt
        )
        val responseDto = api.parseProxy(request, token)
        responseDto.toDomain()
    }
}
