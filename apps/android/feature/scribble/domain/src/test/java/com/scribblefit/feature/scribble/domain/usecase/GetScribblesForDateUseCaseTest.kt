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

class GetScribblesForDateUseCaseTest {

    private val repository: ScribbleRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetScribblesForDateUseCase

    private val testDate = CurrentDate(LocalDateTime.of(2024, 3, 1, 0, 0))

    private fun scribble(id: Long, status: ScribbleStatus) = Scribble(
        id = id,
        rawText = "text $id",
        status = status,
        createdAt = testDate.millis
    )

    @Before
    fun setup() {
        useCase = GetScribblesForDateUseCase(
            repository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns all scribbles for date regardless of status`() = runTest(testDispatcher) {
        val scribbles = listOf(
            scribble(1L, ScribbleStatus.PENDING),
            scribble(2L, ScribbleStatus.SUCCESS),
            scribble(3L, ScribbleStatus.COMPLETED)
        )
        every { repository.getScribblesByDate(testDate.millis) } returns flowOf(scribbles)

        val result = useCase(testDate).first()

        assertEquals(3, result.size)
    }

    @Test
    fun `invoke returns empty list when no scribbles for date`() = runTest(testDispatcher) {
        every { repository.getScribblesByDate(testDate.millis) } returns flowOf(emptyList())

        val result = useCase(testDate).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke delegates to repository with correct date millis`() = runTest(testDispatcher) {
        val specificDate = CurrentDate(LocalDateTime.of(2024, 6, 15, 12, 0))
        every { repository.getScribblesByDate(specificDate.millis) } returns flowOf(emptyList())

        val result = useCase(specificDate).first()

        assertTrue(result.isEmpty())
        // Verifies that the repository was called with the correct millis value
    }

    @Test
    fun `invoke does not filter any scribble by status`() = runTest(testDispatcher) {
        val failed = scribble(1L, ScribbleStatus.FAILED)
        every { repository.getScribblesByDate(testDate.millis) } returns flowOf(listOf(failed))

        val result = useCase(testDate).first()

        assertEquals(1, result.size)
        assertEquals(ScribbleStatus.FAILED, result[0].status)
    }
}
