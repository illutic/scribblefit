package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ParsedWorkoutDto
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScribbleFitProxyEngineTest {

    @Test
    fun `parseWorkout returns success when API returns valid DTO`() = runTest {
        val mockApi = object : ScribbleFitApi {
            override suspend fun getMetadata() = throw UnsupportedOperationException()
            override suspend fun getPromptConfig() = throw UnsupportedOperationException()
            override suspend fun parseProxy(request: com.scribblefit.core.network.model.ParseRequest): ParsedWorkoutDto {
                return ParsedWorkoutDto(
                    date = "2024-03-03",
                    location = "Home",
                    exercises = emptyList()
                )
            }
        }

        val engine = ScribbleFitProxyEngine(mockApi, "test-prompt")
        val result = engine.parseWorkout("Bench 135x5")

        assertTrue(result.isSuccess)
        val workout = result.getOrNull()!!
        assertEquals("2024-03-03", workout.date)
        assertEquals("Home", workout.location)
    }

    @Test
    fun `parseWorkout returns failure when API throws exception`() = runTest {
        val mockApi = object : ScribbleFitApi {
            override suspend fun getMetadata() = throw UnsupportedOperationException()
            override suspend fun getPromptConfig() = throw UnsupportedOperationException()
            override suspend fun parseProxy(request: com.scribblefit.core.network.model.ParseRequest): ParsedWorkoutDto {
                throw Exception("Network Error")
            }
        }

        val engine = ScribbleFitProxyEngine(mockApi, "test-prompt")
        val result = engine.parseWorkout("Bench 135x5")

        assertTrue(result.isFailure)
    }
}
