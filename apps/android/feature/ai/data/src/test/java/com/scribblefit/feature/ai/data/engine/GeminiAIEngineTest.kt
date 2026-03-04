package com.scribblefit.feature.ai.data.engine

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiAIEngineTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parseWorkout returns success when Gemini returns valid JSON`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """
                {
                    "candidates": [
                        {
                            "content": {
                                "parts": [
                                    {
                                        "text": "{\"date\":\"2024-03-03\",\"exercises\":[{\"canonicalName\":\"Bench Press\",\"sets\":[{\"weight\":135.0,\"reps\":5}]}]}"
                                    }
                                ]
                            }
                        }
                    ]
                }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }

        val engine = GeminiAIEngine(
            client = httpClient,
            apiKey = "test-key",
            systemPrompt = "test-prompt",
            json = json
        )

        val result = engine.parseWorkout("Bench 135x5")
        
        assertTrue("Result should be success, but was $result", result.isSuccess)
        val workout = result.getOrNull()!!
        assertEquals("2024-03-03", workout.date)
        assertEquals(1, workout.exercises.size)
        assertEquals("Bench Press", workout.exercises[0].canonicalName)
    }
}
