package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetMuscleDistributionInsightsUseCaseTest {

    private val repository: InsightsRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetMuscleDistributionInsightsUseCase

    private val startDate = CurrentDate(LocalDateTime.of(2024, 1, 1, 0, 0))
    private val endDate = CurrentDate(LocalDateTime.of(2024, 1, 31, 23, 59))

    @Before
    fun setup() {
        useCase = GetMuscleDistributionInsightsUseCase(
            repository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns muscle distribution from repository`() = runTest(testDispatcher) {
        val expected = listOf(
            MuscleGroupDistribution("Chest", 40f),
            MuscleGroupDistribution("Back", 35f),
            MuscleGroupDistribution("Legs", 25f)
        )
        every { repository.getMuscleDistributionInsights(startDate.millis, endDate.millis) } returns flowOf(expected)

        val result = useCase(startDate, endDate).first()

        assertEquals(expected, result)
    }

    @Test
    fun `invoke delegates to repository with correct date millis`() = runTest(testDispatcher) {
        every { repository.getMuscleDistributionInsights(any(), any()) } returns flowOf(emptyList())

        useCase(startDate, endDate).first()

        verify(exactly = 1) { repository.getMuscleDistributionInsights(startDate.millis, endDate.millis) }
    }

    @Test
    fun `invoke returns empty list when no distribution data`() = runTest(testDispatcher) {
        every { repository.getMuscleDistributionInsights(any(), any()) } returns flowOf(emptyList())

        val result = useCase(startDate, endDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke uses start and end date as positional params correctly`() = runTest(testDispatcher) {
        val differentStart = CurrentDate(LocalDateTime.of(2024, 6, 1, 0, 0))
        val differentEnd = CurrentDate(LocalDateTime.of(2024, 6, 30, 23, 59))
        every { repository.getMuscleDistributionInsights(differentStart.millis, differentEnd.millis) } returns flowOf(emptyList())

        useCase(differentStart, differentEnd).first()

        verify(exactly = 1) { repository.getMuscleDistributionInsights(differentStart.millis, differentEnd.millis) }
    }
}
