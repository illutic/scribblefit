package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.TrendDirection
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetExerciseTrendDataUseCaseTest {

    private val repository: ExerciseRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetExerciseTrendDataUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetExerciseTrendDataUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when repository emits exercises, usecase calculates trends correctly`() = runTest {
        // Given
        val exerciseName = "Bench Press"
        val now = System.currentTimeMillis()
        val exercises = listOf(
            mockk<Exercise> {
                every { createdAt } returns now - 1000000
                every { sets } returns listOf(
                    mockk {
                        every { weight } returns 100f
                        every { reps } returns 10
                    }
                )
            },
            mockk<Exercise> {
                every { createdAt } returns now
                every { sets } returns listOf(
                    mockk {
                        every { weight } returns 120f
                        every { reps } returns 10
                    }
                )
            }
        )
        every { repository.getExercisesByNameFlow(exerciseName) } returns flowOf(exercises)

        // When
        val result = useCase(exerciseName, TrendPeriod.ALL).first()

        // Then
        assertTrue(result.isSuccess)
        val trendResult = result.getOrThrow()
        assertEquals(2, trendResult.oneRM.dataPoints.size)
        assertEquals(2, trendResult.volume.dataPoints.size)
        
        assertEquals(160f, trendResult.oneRM.insights.personalBest, 0.01f)
        assertEquals(1200f, trendResult.volume.insights.personalBest, 0.01f)
        
        assertEquals(20f, trendResult.oneRM.insights.percentageChange, 0.01f)
        assertEquals(TrendDirection.IMPROVING, trendResult.oneRM.insights.trendDirection)
    }

    @Test
    fun `when repository emits empty list, usecase returns empty result`() = runTest {
        // Given
        val exerciseName = "Bench Press"
        every { repository.getExercisesByNameFlow(exerciseName) } returns flowOf(emptyList())

        // When
        val result = useCase(exerciseName, TrendPeriod.ALL).first()

        // Then
        assertTrue(result.isSuccess)
        val trendResult = result.getOrThrow()
        assertTrue(trendResult.oneRM.dataPoints.isEmpty())
        assertTrue(trendResult.volume.dataPoints.isEmpty())
        assertEquals(0f, trendResult.oneRM.insights.personalBest)
        assertEquals(0f, trendResult.oneRM.insights.percentageChange)
        assertEquals(TrendDirection.STABLE, trendResult.oneRM.insights.trendDirection)
    }
}
