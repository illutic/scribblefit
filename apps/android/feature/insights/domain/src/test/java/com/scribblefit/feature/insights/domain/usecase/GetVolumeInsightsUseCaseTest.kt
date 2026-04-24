package com.scribblefit.feature.insights.domain.usecase

import app.cash.turbine.test
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class GetVolumeInsightsUseCaseTest {

    private val repository = mockk<InsightsRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = GetVolumeInsightsUseCase(repository, testDispatcher)

    @Test
    fun `when called, should convert dates to millis and return flow from repository`() =
        runTest(testDispatcher) {
            // Given
            val startDate = LocalDate.of(2026, 3, 1)
            val endDate = LocalDate.of(2026, 3, 17)
            val startMillis = startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            val endMillis = endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            val expectedData = listOf(
                VolumeDataPoint(startDate, 1000f),
                VolumeDataPoint(endDate, 1200f)
            )
            every { repository.getVolumeInsights(startMillis, endMillis) } returns flowOf(
                expectedData
            )

            // When & Then
            useCase(startDate, endDate).test {
                assertEquals(expectedData, awaitItem())
                awaitComplete()
            }
        }
}
