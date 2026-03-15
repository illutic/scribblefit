package com.scribblefit.feature.ai.data.di

import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SecureKeyStorage
import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.feature.ai.data.engine.DynamicLLMEngine
import com.scribblefit.feature.ai.data.engine.GeminiAIEngine
import com.scribblefit.feature.ai.data.engine.LocalAIEngine
import com.scribblefit.feature.ai.domain.LLMEngine
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
    ): LocalAIEngine =
        LocalAIEngine(
            generativeModel = generativeModel,
            json = json,
            configRepository = configRepository
        )

    @Provides
    @Singleton
    fun provideLLMEngine(
        geminiEngine: GeminiAIEngine,
        localEngine: LocalAIEngine,
        configRepository: ConfigRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): LLMEngine = DynamicLLMEngine(
        geminiEngine = geminiEngine,
        localEngine = localEngine,
        configRepository = configRepository,
        coroutineDispatcher = coroutineDispatcherProvider.io()
    )
}
