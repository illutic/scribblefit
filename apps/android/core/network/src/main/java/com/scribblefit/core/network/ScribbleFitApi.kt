package com.scribblefit.core.network

import com.scribblefit.core.network.model.ConfigResponse
import com.scribblefit.core.network.model.MetadataResponse
import retrofit2.http.GET

interface ScribbleFitApi {
    @GET("api/sync/metadata")
    suspend fun getMetadata(): MetadataResponse

    @GET("api/config/prompt")
    suspend fun getPromptConfig(): ConfigResponse
}
