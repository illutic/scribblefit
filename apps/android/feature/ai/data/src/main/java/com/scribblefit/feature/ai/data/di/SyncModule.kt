package com.scribblefit.feature.ai.data.di

import android.content.Context
import androidx.work.WorkManager
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.scribblefit.feature.ai.data.engine.DynamicLLMEngine
import com.scribblefit.feature.ai.data.engine.GeminiAIEngine
import com.scribblefit.feature.ai.data.engine.LocalAIEngine
import com.scribblefit.feature.ai.data.engine.OpenAIEngine
import com.scribblefit.feature.ai.data.repository.ConfigRepositoryImpl
import com.scribblefit.feature.ai.data.repository.SyncRepositoryImpl
import com.scribblefit.feature.ai.data.security.SecureKeyStorageImpl
import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.model.SystemConfig
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import com.scribblefit.feature.ai.domain.usecase.ListenForSyncItemsUseCase
import com.scribblefit.feature.ai.domain.usecase.SyncWorkoutUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds @Singleton
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository

    @Binds @Singleton
    abstract fun bindConfigRepository(impl: ConfigRepositoryImpl): ConfigRepository

    @Binds @Singleton
    abstract fun bindSecureKeyStorage(impl: SecureKeyStorageImpl): SecureKeyStorage

    companion object {
        @Provides @Singleton
        fun provideJson(): Json = Json { ignoreUnknownKeys = true; isLenient = true }

        @Provides @Singleton
        fun provideHttpClient(json: Json): HttpClient = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(json)
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
        }

        @Provides @Singleton
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)

        @Provides @Singleton @Named("gemini")
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

        @Provides @Singleton @Named("openai")
        fun provideOpenAIEngine(
            httpClient: HttpClient,
            secureKeyStorage: SecureKeyStorage,
            json: Json
        ): LLMEngine {
            val prompt = SystemConfig.defaultPrompt
            return OpenAIEngine(httpClient, secureKeyStorage, json, prompt)
        }

        @Provides @Singleton
        fun provideGenerativeModel(): GenerativeModel = Generation.getClient()

        @Provides @Singleton
        fun provideLocalEngine(generativeModel: GenerativeModel, json: Json): LocalAIEngine =
            LocalAIEngine(generativeModel, json)

        @Provides @Singleton
        fun provideLLMEngine(
            @Named("openai") openAIEngine: LLMEngine,
            @Named("gemini") geminiEngine: LLMEngine,
            localEngine: LocalAIEngine,
            configRepository: ConfigRepository
        ): LLMEngine = DynamicLLMEngine(openAIEngine, geminiEngine, localEngine, configRepository)

        @Provides @Singleton
        fun provideAnalysisEngine(llmEngine: LLMEngine): AnalysisEngine =
            llmEngine as AnalysisEngine

        @Provides @Singleton
        fun provideSyncWorkoutUseCase(
            syncRepository: SyncRepository,
            llmEngine: LLMEngine
        ): SyncWorkoutUseCase = SyncWorkoutUseCase(syncRepository, llmEngine)

        @Provides @Singleton
        fun provideListenForSyncItemsUseCase(
            syncRepository: SyncRepository
        ): ListenForSyncItemsUseCase = ListenForSyncItemsUseCase(syncRepository)
    }
}
