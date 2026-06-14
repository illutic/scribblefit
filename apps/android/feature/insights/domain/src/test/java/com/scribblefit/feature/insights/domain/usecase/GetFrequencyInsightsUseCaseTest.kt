package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetFrequencyInsightsUseCaseTest {

    private val repository: InsightsRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetFrequencyInsightsUseCase

    private val startDate = CurrentDate(LocalDateTime.of(2024, 1, 1, 0, 0))
    private val endDate = CurrentDate(LocalDateTime.of(2024, 1, 31, 23, 59))

    @Before
    fun setup() {
        useCase = GetFrequencyInsightsUseCase(
            repository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns frequency data from repository`() = runTest(testDispatcher) {
        val expected = FrequencyData(workoutsPerWeek = 4L, totalExercises = 20)
        every { repository.getFrequencyInsights(startDate.millis, endDate.millis) } returns flowOf(expected)

        val result = useCase(startDate, endDate).first()

        assertEquals(expected, result)
    }

    @Test
    fun `invoke delegates to repository with correct date millis`() = runTest(testDispatcher) {
        every { repository.getFrequencyInsights(any(), any()) } returns flowOf(FrequencyData(0, 0))

        useCase(startDate, endDate).first()

        verify(exactly = 1) { repository.getFrequencyInsights(startDate.millis, endDate.millis) }
    }

    @Test
    fun `invoke returns zero frequency when no workouts`() = runTest(testDispatcher) {
        val empty = FrequencyData(workoutsPerWeek = 0, totalExercises = 0)
        every { repository.getFrequencyInsights(any(), any()) } returns flowOf(empty)

        val result = useCase(startDate, endDate).first()

        assertEquals(0L, result.workoutsPerWeek)
        assertEquals(0, result.totalExercises)
    }
}
