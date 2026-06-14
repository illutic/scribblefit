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

class GetPendingScribblesByDateUseCaseTest {

    private val repository: ScribbleRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetPendingScribblesByDateUseCase

    private val testDate = CurrentDate(LocalDateTime.of(2024, 3, 1, 0, 0))

    private fun scribble(id: Long, status: ScribbleStatus) = Scribble(
        id = id,
        rawText = "raw text $id",
        status = status,
        createdAt = testDate.millis
    )

    @Before
    fun setup() {
        useCase = GetPendingScribblesByDateUseCase(
            scribbleRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke filters only PENDING scribbles`() = runTest(testDispatcher) {
        val pending = scribble(1L, ScribbleStatus.PENDING)
        val parsing = scribble(2L, ScribbleStatus.PARSING)
        val success = scribble(3L, ScribbleStatus.SUCCESS)
        val completed = scribble(4L, ScribbleStatus.COMPLETED)
        val failed = scribble(5L, ScribbleStatus.FAILED)

        every { repository.getScribblesByDate(testDate.millis) } returns
                flowOf(listOf(pending, parsing, success, completed, failed))

        val result = useCase(testDate).first()

        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals(ScribbleStatus.PENDING, result[0].status)
    }

    @Test
    fun `invoke returns empty list when no scribbles exist for date`() = runTest(testDispatcher) {
        every { repository.getScribblesByDate(testDate.millis) } returns flowOf(emptyList())

        val result = useCase(testDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns empty list when all scribbles are not PENDING`() = runTest(testDispatcher) {
        val scribbles = listOf(
            scribble(1L, ScribbleStatus.SUCCESS),
            scribble(2L, ScribbleStatus.COMPLETED),
            scribble(3L, ScribbleStatus.FAILED)
        )
        every { repository.getScribblesByDate(testDate.millis) } returns flowOf(scribbles)

        val result = useCase(testDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke returns all when all scribbles are PENDING`() = runTest(testDispatcher) {
        val scribbles = listOf(
            scribble(1L, ScribbleStatus.PENDING),
            scribble(2L, ScribbleStatus.PENDING)
        )
        every { repository.getScribblesByDate(testDate.millis) } returns flowOf(scribbles)

        val result = useCase(testDate).first()

        assertEquals(2, result.size)
    }

    @Test
    fun `invoke passes correct millis to repository`() = runTest(testDispatcher) {
        var capturedDate = 0L
        every { repository.getScribblesByDate(capture(mutableListOf<Long>().also { captured ->
            // Use a different approach to capture
        })) } answers {
            capturedDate = firstArg()
            flowOf(emptyList())
        }
        every { repository.getScribblesByDate(testDate.millis) } answers {
            capturedDate = testDate.millis
            flowOf(emptyList())
        }

        useCase(testDate).first()

        assertEquals(testDate.millis, capturedDate)
    }
}
