package com.scribblefit.core.network

import com.scribblefit.core.network.model.ConfigResponse
import com.scribblefit.core.network.model.MetadataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface ScribbleFitApi {
    suspend fun getMetadata(): MetadataResponse
    suspend fun getPromptConfig(): ConfigResponse
}

class ScribbleFitApiImpl(
    private val client: HttpClient
) : ScribbleFitApi {
    override suspend fun getMetadata(): MetadataResponse {
        return client.get("api/sync/metadata").body()
    }

    override suspend fun getPromptConfig(): ConfigResponse {
        return client.get("api/config/prompt").body()
    }
}
