package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParseRequest
import com.scribblefit.core.network.model.ParsedWorkoutDto
import com.scribblefit.feature.ai.domain.model.AIParsingException
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScribbleFitProxyEngineTest {

    private lateinit var api: ScribbleFitApi
    private lateinit var secureKeyStorage: SecureKeyStorage
    private lateinit var engine: ScribbleFitProxyEngine
    private val systemPrompt = "test-prompt"

    @Before
    fun setup() {
        api = mockk()
        secureKeyStorage = mockk(relaxed = true)
        engine = ScribbleFitProxyEngine(api, secureKeyStorage, systemPrompt)
    }

    @Test
    fun `parseWorkout returns success when API returns valid DTO`() = runTest {
        val rawText = "Bench 135x5"
        val token = "mock-token"
        val expectedDto = ParsedWorkoutDto(
            date = "2024-03-03",
            location = "Home",
            exercises = emptyList()
        )

        coEvery { secureKeyStorage.getAuthToken() } returns token
        coEvery {
            api.parseProxy(ParseRequest(rawText = rawText, prompt = systemPrompt), token)
        } returns expectedDto

        val result = engine.parseWorkout(rawText)

        assertTrue(result.isSuccess)
        val workout = result.getOrNull()!!
        assertEquals("2024-03-03", workout.date)
        assertEquals("Home", workout.location)

        coVerify { api.parseProxy(any(), token) }
    }

    @Test
    fun `parseWorkout returns failure when API throws exception`() = runTest {
        val rawText = "Bench 135x5"
        coEvery { api.parseProxy(any(), any()) } throws Exception("Network Error")

        val result = engine.parseWorkout(rawText)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is AIParsingException)
        assertEquals(
            "AI Parsing failed for text: $rawText. Error: Proxy Failure: Network Error",
            exception?.message
        )
    }
}
