package com.scribblefit.feature.profile.data.di

import com.scribblefit.feature.profile.data.repository.ModelRepositoryImpl
import com.scribblefit.feature.profile.data.repository.UserRepositoryImpl
import com.scribblefit.feature.profile.domain.repository.ModelRepository
import com.scribblefit.feature.profile.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
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
    abstract fun bindModelRepository(impl: ModelRepositoryImpl): ModelRepository
}
