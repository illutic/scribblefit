package com.scribblefit.feature.ai.data.engine

import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import com.google.mlkit.genai.prompt.GenerateContentResponse
import com.google.mlkit.genai.prompt.Candidate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalAIEngineTest {

    private val generativeModel: GenerativeModel = mockk()
    private val json = Json { ignoreUnknownKeys = true }
    private val systemPrompt = "test-prompt"
    private val engine = LocalAIEngine(generativeModel, json, systemPrompt)

    @Test
    fun `parseWorkout returns success when Gemini Nano is available and returns valid JSON`() = runTest {
        val rawText = "Bench 135x5"
        val mockResponseContent = "{\"date\":\"2024-03-03\",\"exercises\":[]}"
        
        val mockCandidate = mockk<Candidate> {
            coEvery { text } returns mockResponseContent
        }
        val mockResponse = mockk<GenerateContentResponse> {
            coEvery { candidates } returns listOf(mockCandidate)
        }

        coEvery { generativeModel.checkStatus() } returns FeatureStatus.AVAILABLE
        coEvery { generativeModel.generateContent(any<String>()) } returns mockResponse

        val result = engine.parseWorkout(rawText)

        assertTrue("Result should be success, but was $result", result.isSuccess)
        assertEquals("2024-03-03", result.getOrNull()?.date)
    }

    @Test
    fun `parseWorkout returns failure when Gemini Nano is unavailable`() = runTest {
        coEvery { generativeModel.checkStatus() } returns FeatureStatus.UNAVAILABLE

        val result = engine.parseWorkout("Bench 135x5")

        assertTrue("Result should be failure", result.isFailure)
    }
}
