package com.scribblefit.core.network

import com.scribblefit.core.network.model.AuthRequest
import com.scribblefit.core.network.model.AuthResponse
import com.scribblefit.core.network.model.ConfigResponse
import com.scribblefit.core.network.model.ExerciseResponse
import com.scribblefit.core.network.model.MetadataResponse
import com.scribblefit.core.network.model.ParseRequest
import com.scribblefit.core.network.model.ParsedWorkoutDto
import com.scribblefit.core.network.model.TelemetryRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

interface ScribbleFitApi {
    suspend fun login(request: AuthRequest): AuthResponse
    suspend fun getMetadata(): MetadataResponse
    suspend fun getPromptConfig(): ConfigResponse
    suspend fun parseProxy(request: ParseRequest, token: String? = null): ParsedWorkoutDto
    suspend fun getExercises(): ExerciseResponse
    suspend fun reportError(request: TelemetryRequest): HttpStatusCode
}

class ScribbleFitApiImpl(
    private val client: HttpClient
) : ScribbleFitApi {
    override suspend fun login(request: AuthRequest): AuthResponse {
        return client.post("api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getMetadata(): MetadataResponse {
        return client.get("api/sync/metadata").body()
    }

    override suspend fun getPromptConfig(): ConfigResponse {
        return client.get("api/config/prompt").body()
    }

    override suspend fun parseProxy(request: ParseRequest, token: String?): ParsedWorkoutDto {
        return client.post("api/parse/proxy") {
            contentType(ContentType.Application.Json)
            if (token != null) {
                header("Authorization", "Bearer $token")
            }
            setBody(request)
        }.body()
    }

    override suspend fun getExercises(): ExerciseResponse {
        return client.get("api/sync/exercises").body()
    }

    override suspend fun reportError(request: TelemetryRequest): HttpStatusCode {
        return client.post("api/telemetry/errors") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.status
    }
}
