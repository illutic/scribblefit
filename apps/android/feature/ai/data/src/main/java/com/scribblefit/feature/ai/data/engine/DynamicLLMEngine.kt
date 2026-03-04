package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named

class DynamicLLMEngine @Inject constructor(
    @param:Named("openai") private val openAIEngine: LLMEngine,
    @param:Named("gemini") private val geminiAIEngine: LLMEngine,
    @param:Named("proxy") private val proxyEngine: LLMEngine,
    private val localAIEngine: LocalAIEngine,
    private val systemConfigDao: SystemConfigDao
) : LLMEngine {

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> {
        val config = systemConfigDao.getConfig().firstOrNull()
        val provider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        
        val engine = when (provider) {
            LLMProvider.OPENAI -> openAIEngine
            LLMProvider.GEMINI -> geminiAIEngine
            LLMProvider.LOCAL -> localAIEngine
            LLMProvider.PROXY -> proxyEngine
        }
        
        return engine.parseWorkout(rawText)
    }
}
