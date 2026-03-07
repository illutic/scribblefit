package com.scribblefit.feature.profile.data.di

import com.scribblefit.feature.profile.data.repository.SettingsRepositoryImpl
import com.scribblefit.feature.profile.data.repository.UserRepositoryImpl
import com.scribblefit.feature.profile.domain.repository.SettingsRepository
import com.scribblefit.feature.profile.domain.repository.UserRepository
import com.scribblefit.feature.profile.domain.usecase.GetUserStatsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideGetUserStatsUseCase(repository: UserRepository): GetUserStatsUseCase {
            return GetUserStatsUseCase(repository)
        }
    }
}
