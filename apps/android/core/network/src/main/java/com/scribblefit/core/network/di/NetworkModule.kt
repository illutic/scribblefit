package com.scribblefit.core.network.di

import com.scribblefit.core.network.NetworkConfig
import com.scribblefit.core.network.NetworkConfigImpl
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.ScribbleFitApiImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkConfig(impl: NetworkConfigImpl): NetworkConfig

    companion object {
        @Provides
        @Singleton
        fun provideHttpClient(networkConfig: NetworkConfig): HttpClient {
            return HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                    })
                }
                install(Logging) {
                    level = LogLevel.BODY
                }
                defaultRequest {
                    url(networkConfig.baseUrl)
                }
            }
        }

        @Provides
        @Singleton
        fun provideScribbleFitApi(client: HttpClient): ScribbleFitApi {
            return ScribbleFitApiImpl(client)
        }
    }
}
