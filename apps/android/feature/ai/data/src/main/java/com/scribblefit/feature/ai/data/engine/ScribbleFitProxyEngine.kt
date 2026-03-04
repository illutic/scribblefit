package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParseRequest
import javax.inject.Inject

class ScribbleFitProxyEngine @Inject constructor(
    private val api: ScribbleFitApi,
    private val systemPrompt: String
) : LLMEngine {
    
    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> = runCatching {
        val request = ParseRequest(
            rawText = rawText,
            prompt = systemPrompt
        )
        val responseDto = api.parseProxy(request)
        responseDto.toDomain()
    }
}
