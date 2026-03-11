package com.scribblefit.feature.profile.data.repository

import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.feature.profile.domain.repository.ModelRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

private const val OPENAI_MODELS_URL = "https://api.openai.com/v1/models"
private const val GEMINI_MODELS_URL = "https://generativelanguage.googleapis.com/v1beta/models"

@Singleton
class ModelRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : ModelRepository {

    @InternalSerializationApi
    override suspend fun fetchModels(provider: LLMProvider, apiKey: String): List<String> =
        when (provider) {
            LLMProvider.OPENAI -> fetchOpenAIModels(apiKey)
            LLMProvider.GEMINI -> fetchGeminiModels(apiKey)
            else -> emptyList()
        }

    @InternalSerializationApi
    private suspend fun fetchOpenAIModels(apiKey: String): List<String> = runCatching {
        @Serializable data class ModelData(val id: String)
        @Serializable data class ModelsResponse(val data: List<ModelData>)
        val response = httpClient.get(OPENAI_MODELS_URL) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }
        response.body<ModelsResponse>().data
            .map { it.id }
            .filter { id -> id.startsWith("gpt-") || id.startsWith("o1") || id.startsWith("o3") }
            .sorted()
    }.getOrDefault(emptyList())

    @InternalSerializationApi
    private suspend fun fetchGeminiModels(apiKey: String): List<String> = runCatching {
        @Serializable data class GeminiModel(val name: String, val supportedGenerationMethods: List<String>)
        @Serializable data class GeminiModelsResponse(val models: List<GeminiModel>)
        val response = httpClient.get("$GEMINI_MODELS_URL?key=$apiKey")
        response.body<GeminiModelsResponse>().models
            .filter { it.supportedGenerationMethods.contains("generateContent") }
            .map { it.name.removePrefix("models/") }
            .sorted()
    }.getOrDefault(emptyList())
}
