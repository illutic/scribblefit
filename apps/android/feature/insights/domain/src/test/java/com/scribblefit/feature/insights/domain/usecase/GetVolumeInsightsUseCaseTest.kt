package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GetVolumeInsightsUseCaseTest {

    private val repository = mockk<InsightsRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = GetVolumeInsightsUseCase(repository, testDispatcher)

    @Test
    fun `invoke returns success when repository returns data`() = runTest(testDispatcher) {
        // Given
        val startDate = CurrentDate(LocalDate.now().minusDays(7))
        val endDate = CurrentDate(LocalDate.now())
        val expectedData = listOf(VolumeDataPoint(1000L, 100f))

        coEvery {
            repository.getVolumeInsights(startDate.startOfDayInMillis, endDate.startOfDayInMillis)
        } returns flowOf(expectedData)

        // When
        val result = useCase(startDate, endDate)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedData, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        // Given
        val startDate = CurrentDate(LocalDate.now().minusDays(7))
        val endDate = CurrentDate(LocalDate.now())
        val exception = RuntimeException("DB Error")

        coEvery {
            repository.getVolumeInsights(startDate.startOfDayInMillis, endDate.startOfDayInMillis)
        } throws exception

        // When
        val result = useCase(startDate, endDate)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
