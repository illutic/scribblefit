package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.ai.model.AIParsingException
import com.scribblefit.core.ai.model.ParsedWorkout
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

class OpenAIEngineTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parseWorkout returns success when OpenAI returns valid JSON`() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = """
                {
                    "choices": [
                        {
                            "message": {
                                "role": "assistant",
                                "content": "{\"date\":\"2024-03-03\",\"exercises\":[{\"canonicalName\":\"Bench Press\",\"sets\":[{\"weight\":135.0,\"reps\":5}]}]}"
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
            install(ContentNegotiation) {
                json(json)
            }
        }

        val engine = OpenAIEngine(
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
        assertEquals(135.0, workout.exercises[0].sets[0].weight, 0.0)
    }

    @Test
    fun `parseWorkout throws AIParsingException when OpenAI returns invalid JSON`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """
                {
                    "choices": [
                        {
                            "message": {
                                "role": "assistant",
                                "content": "invalid-json"
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

        val engine = OpenAIEngine(httpClient, "test-key", "test-prompt", json)

        val result = engine.parseWorkout("Bench 135x5")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AIParsingException)
    }

    @Test
    fun `parseWorkout returns failure when OpenAI returns error`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "Error",
                status = HttpStatusCode.InternalServerError
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }

        val engine = OpenAIEngine(httpClient, "test-key", "test-prompt", json)

        val result = engine.parseWorkout("Bench 135x5")
        assertTrue(result.isFailure)
    }
}
