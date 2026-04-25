package com.scribblefit.feature.ai.data.di

import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.feature.ai.data.engine.GeminiAIEngine
import com.scribblefit.feature.ai.data.engine.LocalAIEngine
import com.scribblefit.feature.ai.data.engine.RoutingLLMEngine
import com.scribblefit.feature.ai.domain.CloudEngine
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.LocalEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AIModule {

    @Provides
    @Singleton
    @CloudEngine
    fun provideGeminiEngine(
        json: Json,
        configRepository: ConfigRepository
    ): LLMEngine {
        return GeminiAIEngine(
            configRepository = configRepository,
            json = json
        )
    }

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel = Generation.getClient()

    @Provides
    @Singleton
    @LocalEngine
    fun provideLocalEngine(
        generativeModel: GenerativeModel,
        json: Json,
        configRepository: ConfigRepository
    ): LLMEngine =
        LocalAIEngine(
            generativeModel = generativeModel,
            json = json,
            configRepository = configRepository
        )

    @Provides
    @Singleton
    fun provideLLMEngine(
        @CloudEngine geminiEngine: LLMEngine,
        @LocalEngine localEngine: LLMEngine,
        configRepository: ConfigRepository,
    ): LLMEngine = RoutingLLMEngine(
        geminiEngine = geminiEngine,
        localEngine = localEngine,
        configRepository = configRepository
    )
}
