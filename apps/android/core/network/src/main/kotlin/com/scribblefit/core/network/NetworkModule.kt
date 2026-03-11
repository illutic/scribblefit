package com.scribblefit.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = defaultJson()

    @Provides
    @Singleton
    fun provideBaseHttpClient(
        json: Json
    ): HttpClient = buildBaseHttpClient(json)
}