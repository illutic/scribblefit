package com.scribblefit.feature.ai.data.di

import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.feature.ai.data.engine.DynamicLLMEngine
import com.scribblefit.feature.ai.data.engine.GeminiAIEngine
import com.scribblefit.feature.ai.data.engine.LocalAIEngine
import com.scribblefit.feature.ai.data.engine.OpenAIEngine
import com.scribblefit.feature.ai.data.engine.ScribbleFitProxyEngine
import com.scribblefit.feature.ai.data.repository.AuthRepositoryImpl
import com.scribblefit.feature.ai.data.repository.ConfigRepositoryImpl
import com.scribblefit.feature.ai.data.repository.SyncRepositoryImpl
import com.scribblefit.feature.ai.data.repository.TelemetryRepositoryImpl
import com.scribblefit.feature.ai.data.security.SecureKeyStorageImpl
import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.AuthRepository
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.engine.TelemetryRepository
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import com.scribblefit.feature.ai.domain.usecase.ListenForSyncItemsUseCase
import com.scribblefit.feature.ai.domain.usecase.SyncWorkoutUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    @Singleton
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository

    @Binds
    @Singleton
    abstract fun bindConfigRepository(impl: ConfigRepositoryImpl): ConfigRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTelemetryRepository(impl: TelemetryRepositoryImpl): TelemetryRepository

    @Binds
    @Singleton
    abstract fun bindSecureKeyStorage(impl: SecureKeyStorageImpl): SecureKeyStorage

    companion object {
        @Provides
        @Singleton
        fun provideGenerativeModel(): GenerativeModel {
            return Generation.getClient()
        }

        @Provides
        @Singleton
        @Named("openai")
        fun provideOpenAIEngine(
            @Named("base") client: HttpClient,
            secureKeyStorage: SecureKeyStorage,
            configRepository: ConfigRepository,
            json: Json
        ): LLMEngine {
            return OpenAIEngine(client, secureKeyStorage, configRepository, json)
        }

        @Provides
        @Singleton
        @Named("gemini")
        fun provideGeminiAIEngine(
            @Named("base") client: HttpClient,
            secureKeyStorage: SecureKeyStorage,
            configRepository: ConfigRepository,
            json: Json
        ): LLMEngine {
            return GeminiAIEngine(client, secureKeyStorage, configRepository, json)
        }

        @Provides
        @Singleton
        @Named("proxy")
        fun provideProxyEngine(
            api: ScribbleFitApi,
            secureKeyStorage: SecureKeyStorage,
            configRepository: ConfigRepository
        ): LLMEngine {
            return ScribbleFitProxyEngine(api, secureKeyStorage, configRepository)
        }

        @Provides
        @Singleton
        fun provideLLMEngine(
            @Named("openai") openAIEngine: LLMEngine,
            @Named("gemini") geminiAIEngine: LLMEngine,
            @Named("proxy") proxyEngine: LLMEngine,
            localAIEngine: LocalAIEngine,
            configRepository: ConfigRepository
        ): LLMEngine {
            return DynamicLLMEngine(
                openAIEngine,
                geminiAIEngine,
                proxyEngine,
                localAIEngine,
                configRepository
            )
        }

        @Provides
        @Singleton
        fun provideAnalysisEngine(llmEngine: LLMEngine): AnalysisEngine {
            return llmEngine as AnalysisEngine
        }

        @Provides
        @Singleton
        fun provideSyncWorkoutUseCase(
            syncRepository: SyncRepository,
            telemetryRepository: TelemetryRepository,
            engine: LLMEngine,
            configRepository: ConfigRepository
        ): SyncWorkoutUseCase {
            return SyncWorkoutUseCase(syncRepository, telemetryRepository, engine, configRepository)
        }

        @Provides
        @Singleton
        fun provideListenForSyncItemsUseCase(
            syncRepository: SyncRepository
        ): ListenForSyncItemsUseCase {
            return ListenForSyncItemsUseCase(syncRepository)
        }
    }
}
