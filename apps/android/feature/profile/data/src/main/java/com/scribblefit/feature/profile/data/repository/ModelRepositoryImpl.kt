package com.scribblefit.feature.profile.data.repository

import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.profile.domain.repository.ModelRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ModelRepositoryImpl @Inject constructor(
    @Named("base") private val client: HttpClient
) : ModelRepository {

    override suspend fun fetchModels(provider: LLMProvider, apiKey: String): List<String> {
        return when (provider) {
            LLMProvider.OPENAI -> fetchOpenAIModels(apiKey)
            LLMProvider.GEMINI -> fetchGeminiModels(apiKey)
            else -> emptyList()
        }
    }

    private suspend fun fetchOpenAIModels(apiKey: String): List<String> {
        @Serializable
        data class ModelItem(val id: String)
        @Serializable
        data class ModelList(val data: List<ModelItem>)

        val response = client.get("https://api.openai.com/v1/models") {
            header("Authorization", "Bearer $apiKey")
        }.body<ModelList>()

        return response.data
            .map { it.id }
            .filter { it.startsWith("gpt-") || it.startsWith("o1") || it.startsWith("o3") }
            .sorted()
    }

    private suspend fun fetchGeminiModels(apiKey: String): List<String> {
        @Serializable
        data class ModelItem(
            val name: String,
            val supportedGenerationMethods: List<String> = emptyList()
        )
        @Serializable
        data class ModelList(val models: List<ModelItem>)

        val response = client.get("https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey")
            .body<ModelList>()

        return response.models
            .filter { it.supportedGenerationMethods.contains("generateContent") }
            .map { it.name }
            .sorted()
    }
}
