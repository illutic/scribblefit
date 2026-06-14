package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class GetVolumeInsightsUseCaseTest {

    private val repository = mockk<InsightsRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = GetVolumeInsightsUseCase(repository, testDispatcher)

    @Test
    fun `invoke returns success when repository returns data`() = runTest(testDispatcher) {
        // Given
        val startDate = CurrentDate(LocalDateTime.now().minusDays(7))
        val endDate = CurrentDate(LocalDateTime.now())
        val expectedData = listOf(VolumeDataPoint(1000L, 100f))

        coEvery {
            repository.getVolumeInsights(startDate.millis, endDate.millis)
        } returns flowOf(expectedData)

        // When
        val result = useCase(startDate, endDate).first()

        // Then
        assertEquals(expectedData, result)
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        // Given
        val startDate = CurrentDate(LocalDateTime.now().minusDays(7))
        val endDate = CurrentDate(LocalDateTime.now())
        val exception = RuntimeException("DB Error")

        coEvery {
            repository.getVolumeInsights(startDate.millis, endDate.millis)
        } returns flow { throw exception }

        // When
        var threw = false
        try {
            useCase(startDate, endDate).first()
        } catch (e: Exception) {
            threw = true
            assertEquals(exception, e)
        }

        // Then
        assertTrue(threw)
    }
}
