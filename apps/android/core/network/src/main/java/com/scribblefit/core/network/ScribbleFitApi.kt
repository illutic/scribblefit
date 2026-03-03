package com.scribblefit.core.network

import com.scribblefit.core.network.model.ConfigResponse
import com.scribblefit.core.network.model.MetadataResponse
import com.scribblefit.core.network.model.ParseRequest
import com.scribblefit.core.network.model.ParsedWorkoutDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface ScribbleFitApi {
    suspend fun getMetadata(): MetadataResponse
    suspend fun getPromptConfig(): ConfigResponse
    suspend fun parseProxy(request: ParseRequest): ParsedWorkoutDto
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

    override suspend fun parseProxy(request: ParseRequest): ParsedWorkoutDto {
        return client.post("api/parse/proxy") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
