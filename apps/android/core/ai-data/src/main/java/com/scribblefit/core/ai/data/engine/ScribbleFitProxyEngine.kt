package com.scribblefit.core.ai.data.engine

import com.scribblefit.core.ai.data.mapper.toDomain
import com.scribblefit.core.ai.engine.LLMEngine
import com.scribblefit.core.ai.model.ParsedWorkout
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParseRequest
import javax.inject.Inject

class ScribbleFitProxyEngine @Inject constructor(
    private val api: ScribbleFitApi,
    private val systemPrompt: String
) : LLMEngine {
    
    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> {
        return try {
            val request = ParseRequest(
                rawText = rawText,
                prompt = systemPrompt
            )
            val responseDto = api.parseProxy(request)
            Result.success(responseDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
