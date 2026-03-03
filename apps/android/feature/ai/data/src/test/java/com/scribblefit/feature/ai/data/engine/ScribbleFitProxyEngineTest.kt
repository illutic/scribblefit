package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParseRequest
import com.scribblefit.core.network.model.ParsedWorkoutDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScribbleFitProxyEngineTest {

    private val api: ScribbleFitApi = mockk()
    private val systemPrompt = "test-prompt"
    private val engine = ScribbleFitProxyEngine(api, systemPrompt)

    @Test
    fun `parseWorkout returns success when API returns valid DTO`() = runTest {
        val rawText = "Bench 135x5"
        val expectedDto = ParsedWorkoutDto(
            date = "2024-03-03",
            location = "Home",
            exercises = emptyList()
        )

        coEvery { 
            api.parseProxy(ParseRequest(rawText = rawText, prompt = systemPrompt)) 
        } returns expectedDto

        val result = engine.parseWorkout(rawText)

        assertTrue(result.isSuccess)
        val workout = result.getOrNull()!!
        assertEquals("2024-03-03", workout.date)
        assertEquals("Home", workout.location)
        
        coVerify { api.parseProxy(any()) }
    }

    @Test
    fun `parseWorkout returns failure when API throws exception`() = runTest {
        coEvery { api.parseProxy(any()) } throws Exception("Network Error")

        val result = engine.parseWorkout("Bench 135x5")

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }
}
