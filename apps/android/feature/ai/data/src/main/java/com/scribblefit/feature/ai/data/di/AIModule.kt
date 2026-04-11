package com.scribblefit.feature.ai.data.di

import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SecureKeyStorage
import com.scribblefit.feature.ai.data.engine.LLMEngineProxyImpl
import com.scribblefit.feature.ai.data.engine.GeminiAIEngine
import com.scribblefit.feature.ai.data.engine.LocalAIEngine
import com.scribblefit.feature.ai.domain.LLMEngineProxy
import com.scribblefit.feature.ai.domain.LocalLLMEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AIModule {

    @Provides
    @Singleton
    fun provideGeminiEngine(
        httpClient: HttpClient,
        secureKeyStorage: SecureKeyStorage,
        json: Json,
        configRepository: ConfigRepository
    ): GeminiAIEngine {
        return GeminiAIEngine(
            httpClient = httpClient,
            secureKeyStorage = secureKeyStorage,
            configRepository = configRepository,
            json = json
        )
    }

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel = Generation.getClient()

    @Provides
    @Singleton
    fun provideLocalEngine(
        generativeModel: GenerativeModel,
        json: Json,
        configRepository: ConfigRepository
    ): LocalLLMEngine =
        LocalAIEngine(
            generativeModel = generativeModel,
            json = json,
            configRepository = configRepository
        )

    @Provides
    @Singleton
    fun provideLLMEngine(
        geminiEngine: GeminiAIEngine,
        localEngine: LocalLLMEngine,
        configRepository: ConfigRepository,
    ): LLMEngineProxy = LLMEngineProxyImpl(
        geminiEngine = geminiEngine,
        localEngine = localEngine,
        configRepository = configRepository
    )
}
