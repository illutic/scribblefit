package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetScribblesInRangeUseCaseTest {

    private val repository: ScribbleRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetScribblesInRangeUseCase

    private val startDate = CurrentDate(LocalDateTime.of(2024, 1, 1, 0, 0))
    private val endDate = CurrentDate(LocalDateTime.of(2024, 1, 31, 23, 59))

    private fun scribble(id: Long, status: ScribbleStatus) = Scribble(
        id = id,
        rawText = "text $id",
        status = status,
        createdAt = startDate.millis + id
    )

    @Before
    fun setup() {
        useCase = GetScribblesInRangeUseCase(
            repository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns only COMPLETED scribbles`() = runTest(testDispatcher) {
        val scribbles = listOf(
            scribble(1L, ScribbleStatus.COMPLETED),
            scribble(2L, ScribbleStatus.PENDING),
            scribble(3L, ScribbleStatus.SUCCESS),
            scribble(4L, ScribbleStatus.FAILED),
            scribble(5L, ScribbleStatus.COMPLETED)
        )
        every { repository.getScribblesInRange(startDate.millis, endDate.millis) } returns flowOf(scribbles)

        val result = useCase(startDate, endDate).first()

        assertEquals(2, result.size)
        assertTrue(result.all { it.status == ScribbleStatus.COMPLETED })
    }

    @Test
    fun `invoke returns empty list when no scribbles in range`() = runTest(testDispatcher) {
        every { repository.getScribblesInRange(startDate.millis, endDate.millis) } returns flowOf(emptyList())

        val result = useCase(startDate, endDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns empty list when no scribbles are COMPLETED`() = runTest(testDispatcher) {
        val scribbles = listOf(
            scribble(1L, ScribbleStatus.PENDING),
            scribble(2L, ScribbleStatus.PARSING),
            scribble(3L, ScribbleStatus.SUCCESS),
            scribble(4L, ScribbleStatus.FAILED)
        )
        every { repository.getScribblesInRange(startDate.millis, endDate.millis) } returns flowOf(scribbles)

        val result = useCase(startDate, endDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke passes correct start and end date millis to repository`() = runTest(testDispatcher) {
        var capturedStart = 0L
        var capturedEnd = 0L
        every { repository.getScribblesInRange(any(), any()) } answers {
            capturedStart = firstArg()
            capturedEnd = secondArg()
            flowOf(emptyList())
        }

        useCase(startDate, endDate).first()

        assertEquals(startDate.millis, capturedStart)
        assertEquals(endDate.millis, capturedEnd)
    }

    @Test
    fun `invoke returns all COMPLETED scribbles when all are completed`() = runTest(testDispatcher) {
        val scribbles = listOf(
            scribble(1L, ScribbleStatus.COMPLETED),
            scribble(2L, ScribbleStatus.COMPLETED)
        )
        every { repository.getScribblesInRange(startDate.millis, endDate.millis) } returns flowOf(scribbles)

        val result = useCase(startDate, endDate).first()

        assertEquals(2, result.size)
    }
}
