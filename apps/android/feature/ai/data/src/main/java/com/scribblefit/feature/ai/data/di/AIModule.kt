package com.scribblefit.feature.ai.data.di

import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.feature.ai.data.engine.DynamicLLMEngine
import com.scribblefit.feature.ai.data.engine.GeminiAIEngine
import com.scribblefit.feature.ai.data.engine.LocalAIEngine
import com.scribblefit.feature.ai.data.engine.OpenAIEngine
import com.scribblefit.feature.ai.data.security.SecureKeyStorageImpl
import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AIModule {

    @Binds
    @Singleton
    abstract fun bindSecureKeyStorage(impl: SecureKeyStorageImpl): SecureKeyStorage

    companion object {

        @Provides
        @Singleton
        @GeminiLLMEngine
        fun provideGeminiEngine(
            httpClient: HttpClient,
            secureKeyStorage: SecureKeyStorage,
            json: Json,
            configRepository: ConfigRepository
        ): LLMEngine {
            return GeminiAIEngine(
                httpClient = httpClient,
                secureKeyStorage = secureKeyStorage,
                configRepository = configRepository,
                json = json
            )
        }

        @Provides
        @Singleton
        @OpenAILLMEngine
        fun provideOpenAIEngine(
            httpClient: HttpClient,
            secureKeyStorage: SecureKeyStorage,
            json: Json,
            configRepository: ConfigRepository
        ): LLMEngine {
            return OpenAIEngine(
                httpClient = httpClient,
                secureKeyStorage = secureKeyStorage,
                json = json,
                configRepository = configRepository
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
            @OpenAILLMEngine openAIEngine: LLMEngine,
            @GeminiLLMEngine geminiEngine: LLMEngine,
            localEngine: LocalAIEngine,
            configRepository: ConfigRepository,
            coroutineDispatcherProvider: CoroutineDispatcherProvider
        ): LLMEngine = DynamicLLMEngine(
            openAIEngine = openAIEngine,
            geminiEngine = geminiEngine,
            localEngine = localEngine,
            configRepository = configRepository,
            coroutineDispatcher = coroutineDispatcherProvider.io()
        )

        @Provides
        @Singleton
        fun provideAnalysisEngine(llmEngine: LLMEngine): AnalysisEngine =
            llmEngine as AnalysisEngine
    }
}
