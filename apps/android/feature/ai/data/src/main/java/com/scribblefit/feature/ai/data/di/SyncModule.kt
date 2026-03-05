package com.scribblefit.feature.ai.data.di

import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.feature.ai.data.engine.GeminiAIEngine
import com.scribblefit.feature.ai.data.engine.OpenAIEngine
import com.scribblefit.feature.ai.data.engine.ScribbleFitProxyEngine
import com.scribblefit.feature.ai.data.repository.SyncRepositoryImpl
import com.scribblefit.feature.ai.data.repository.ConfigRepositoryImpl
import com.scribblefit.feature.ai.data.repository.AuthRepositoryImpl
import com.scribblefit.feature.ai.data.security.SecureKeyStorageImpl
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.repository.SyncRepository
import com.scribblefit.feature.ai.domain.repository.ConfigRepository
import com.scribblefit.feature.ai.domain.repository.AuthRepository
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import kotlinx.coroutines.runBlocking
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
    abstract fun bindSecureKeyStorage(impl: SecureKeyStorageImpl): SecureKeyStorage

    companion object {
        private const val SYSTEM_PROMPT = """
            You are ScribbleFit AI, a fitness parsing assistant. 
            Your goal is to take raw, messy gym shorthand and parse it into a structured JSON format.
            Extract the date, location, and a list of exercises.
            Each exercise should have a canonical name and a list of sets.
            Each set should have weight, reps, rpe (optional), and notes (optional).
        """

        @Provides
        @Singleton
        @Named("openai")
        fun provideOpenAIEngine(
            @Named("base") client: HttpClient,
            secureKeyStorage: SecureKeyStorage,
            json: Json
        ): LLMEngine {
            val apiKey = runBlocking { secureKeyStorage.getApiKey() } ?: ""
            return OpenAIEngine(client, apiKey, SYSTEM_PROMPT, json)
        }

        @Provides
        @Singleton
        @Named("gemini")
        fun provideGeminiAIEngine(
            @Named("base") client: HttpClient,
            secureKeyStorage: SecureKeyStorage,
            json: Json
        ): LLMEngine {
            val apiKey = runBlocking { secureKeyStorage.getApiKey() } ?: ""
            return GeminiAIEngine(client, apiKey, SYSTEM_PROMPT, json)
        }

        @Provides
        @Singleton
        @Named("proxy")
        fun provideProxyEngine(
            api: ScribbleFitApi,
            secureKeyStorage: SecureKeyStorage
        ): LLMEngine {
            return ScribbleFitProxyEngine(api, secureKeyStorage, SYSTEM_PROMPT)
        }

        @Provides
        @Singleton
        fun provideLLMEngine(
            @Named("openai") openAIEngine: LLMEngine,
            @Named("gemini") geminiAIEngine: LLMEngine,
            @Named("proxy") proxyEngine: LLMEngine,
            localAIEngine: com.scribblefit.feature.ai.data.engine.LocalAIEngine,
            systemConfigDao: com.scribblefit.core.database.dao.SystemConfigDao
        ): LLMEngine {
            return com.scribblefit.feature.ai.data.engine.DynamicLLMEngine(
                openAIEngine,
                geminiAIEngine,
                proxyEngine,
                localAIEngine,
                systemConfigDao
            )
        }
    }
}
