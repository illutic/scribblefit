package com.scribblefit.feature.settings.data.di

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SecureKeyStorage
import com.scribblefit.feature.ai.domain.LLMEngineProxy
import com.scribblefit.feature.ai.domain.LocalLLMEngine
import com.scribblefit.feature.settings.data.SettingsRepositoryImpl
import com.scribblefit.feature.settings.domain.CheckLocalSupportUseCase
import com.scribblefit.feature.settings.domain.ClearUserDataUseCase
import com.scribblefit.feature.settings.domain.ExportUserDataUseCase
import com.scribblefit.feature.settings.domain.GetAvailableModelsUseCase
import com.scribblefit.feature.settings.domain.SettingsRepository
import com.scribblefit.feature.settings.domain.TestConnectionUseCase
import com.scribblefit.feature.settings.domain.UpdateApiKeyUseCase
import com.scribblefit.feature.settings.domain.UpdateSystemConfigUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsDataModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository = impl

    @Provides
    fun provideClearUserDataUseCase(
        repository: SettingsRepository
    ): ClearUserDataUseCase = ClearUserDataUseCase(repository)

    @Provides
    fun provideExportUserDataUseCase(
        repository: SettingsRepository
    ): ExportUserDataUseCase = ExportUserDataUseCase(repository)

    @Provides
    fun provideTestConnectionUseCase(
        repository: SettingsRepository
    ): TestConnectionUseCase = TestConnectionUseCase(repository)

    @Provides
    fun provideUpdateSystemConfigUseCase(
        configRepository: ConfigRepository
    ): UpdateSystemConfigUseCase = UpdateSystemConfigUseCase(configRepository)

    @Provides
    fun provideUpdateApiKeyUseCase(
        secureKeyStorage: SecureKeyStorage
    ): UpdateApiKeyUseCase = UpdateApiKeyUseCase(secureKeyStorage)

    @Provides
    fun provideGetAvailableModelsUseCase(
        llmEngineProxy: LLMEngineProxy
    ): GetAvailableModelsUseCase = GetAvailableModelsUseCase(llmEngineProxy)

    @Provides
    fun provideCheckLocalSupportUseCase(
        localEngine: LocalLLMEngine
    ): CheckLocalSupportUseCase = CheckLocalSupportUseCase(localEngine)
}
