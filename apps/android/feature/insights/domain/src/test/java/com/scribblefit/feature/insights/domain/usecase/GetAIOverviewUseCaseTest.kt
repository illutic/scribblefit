package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.InsightType
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

class GetAIOverviewUseCaseTest {

    private val repository: InsightsRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetAIOverviewUseCase

    private val testDate = CurrentDate(LocalDateTime.of(2024, 6, 10, 12, 0))

    @Before
    fun setup() {
        useCase = GetAIOverviewUseCase(
            repository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke with explicit dates returns insights from repository`() = runTest(testDispatcher) {
        val startDate = CurrentDate(testDate.date.minusDays(7))
        val expectedInsights = listOf(AIInsight(insightType = InsightType.SUMMARY, text = "Great week!"))
        every { repository.getAIOverview(startDate.millis, testDate.millis) } returns flowOf(expectedInsights)

        val result = useCase(startDate, testDate).first()

        assertEquals(expectedInsights, result)
    }

    @Test
    fun `invoke with currentDate and lookUpDays delegates with correct dates`() = runTest(testDispatcher) {
        val startDate = CurrentDate(testDate.date.minusDays(7))
        every { repository.getAIOverview(startDate.millis, testDate.millis) } returns flowOf(emptyList())

        useCase(currentDate = testDate, lookUpDays = 7).first()

        verify(exactly = 1) { repository.getAIOverview(startDate.millis, testDate.millis) }
    }

    @Test
    fun `invoke returns empty list when no insights available`() = runTest(testDispatcher) {
        val startDate = CurrentDate(testDate.date.minusDays(7))
        every { repository.getAIOverview(any(), any()) } returns flowOf(emptyList())

        val result = useCase(startDate, testDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke with lookUpDays 14 uses correct start date`() = runTest(testDispatcher) {
        val startDate = CurrentDate(testDate.date.minusDays(14))
        every { repository.getAIOverview(startDate.millis, testDate.millis) } returns flowOf(emptyList())

        useCase(currentDate = testDate, lookUpDays = 14).first()

        verify(exactly = 1) { repository.getAIOverview(startDate.millis, testDate.millis) }
    }

    @Test
    fun `invoke returns multiple insights`() = runTest(testDispatcher) {
        val startDate = CurrentDate(testDate.date.minusDays(7))
        val insights = listOf(
            AIInsight(InsightType.SUMMARY, "Week summary"),
            AIInsight(InsightType.TREND, "Volume increased"),
            AIInsight(InsightType.ADVICE, "Rest more")
        )
        every { repository.getAIOverview(any(), any()) } returns flowOf(insights)

        val result = useCase(startDate, testDate).first()

        assertEquals(3, result.size)
    }
}
