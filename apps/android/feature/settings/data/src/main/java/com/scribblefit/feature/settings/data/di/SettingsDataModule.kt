package com.scribblefit.feature.settings.data.di

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.database.dao.ScribbleTrackerDao
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.LocalEngine
import com.scribblefit.feature.settings.data.SettingsRepositoryImpl
import com.scribblefit.feature.settings.domain.CheckLocalSupportUseCase
import com.scribblefit.feature.settings.domain.ClearUserDataUseCase
import com.scribblefit.feature.settings.domain.ExportUserDataUseCase
import com.scribblefit.feature.settings.domain.SettingsRepository
import com.scribblefit.feature.settings.domain.UpdateSystemConfigUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SettingsDataModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(
        scribbleTrackerDao: ScribbleTrackerDao,
        workoutDao: WorkoutDao,
        json: Json
    ): SettingsRepository = SettingsRepositoryImpl(
        scribbleTrackerDao = scribbleTrackerDao,
        workoutDao = workoutDao,
        json = json
    )

    @Provides
    @Singleton
    fun provideUpdateSystemConfigUseCase(
        configRepository: ConfigRepository
    ): UpdateSystemConfigUseCase = UpdateSystemConfigUseCase(configRepository)

    @Provides
    @Singleton
    fun provideClearUserDataUseCase(
        repository: SettingsRepository
    ): ClearUserDataUseCase = ClearUserDataUseCase(repository)

    @Provides
    @Singleton
    fun provideExportUserDataUseCase(
        repository: SettingsRepository
    ): ExportUserDataUseCase = ExportUserDataUseCase(repository)

    @Provides
    @Singleton
    fun provideCheckLocalSupportUseCase(
        @LocalEngine localLLMEngine: LLMEngine
    ): CheckLocalSupportUseCase = CheckLocalSupportUseCase(localLLMEngine)
}
