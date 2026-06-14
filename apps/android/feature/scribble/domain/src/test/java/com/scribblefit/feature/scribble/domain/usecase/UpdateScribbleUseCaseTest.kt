package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateScribbleUseCaseTest {

    private val repository: ScribbleRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: UpdateScribbleUseCase

    private val existingScribble = Scribble(
        id = 1L,
        rawText = "bench press 3x10",
        status = ScribbleStatus.PENDING,
        createdAt = 1_000_000L
    )

    @Before
    fun setup() {
        useCase = UpdateScribbleUseCase(
            scribbleRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success when scribble exists and update succeeds`() = runTest(testDispatcher) {
        every { repository.getScribble(1L) } returns flowOf(existingScribble)

        val result = useCase(existingScribble)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke calls updateScribble on repository`() = runTest(testDispatcher) {
        every { repository.getScribble(1L) } returns flowOf(existingScribble)
        val updatedScribble = existingScribble.copy(rawText = "squat 5x5")

        useCase(updatedScribble)

        coVerify(exactly = 1) { repository.updateScribble(updatedScribble) }
    }

    @Test
    fun `invoke returns failure when scribble does not exist`() = runTest(testDispatcher) {
        every { repository.getScribble(99L) } returns flowOf()
        val nonExistentScribble = existingScribble.copy(id = 99L)

        val result = useCase(nonExistentScribble)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `invoke does not call updateScribble when scribble not found`() = runTest(testDispatcher) {
        every { repository.getScribble(99L) } returns flowOf()
        val nonExistentScribble = existingScribble.copy(id = 99L)

        useCase(nonExistentScribble)

        coVerify(exactly = 0) { repository.updateScribble(any()) }
    }

    @Test
    fun `invoke returns failure when repository update throws`() = runTest(testDispatcher) {
        every { repository.getScribble(1L) } returns flowOf(existingScribble)
        coEvery { repository.updateScribble(any()) } throws RuntimeException("DB error")

        val result = useCase(existingScribble)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke passes the exact scribble to update`() = runTest(testDispatcher) {
        val updatedScribble = existingScribble.copy(status = ScribbleStatus.COMPLETED, rawText = "new text")
        every { repository.getScribble(1L) } returns flowOf(existingScribble)
        val slot = slot<Scribble>()
        coEvery { repository.updateScribble(capture(slot)) } returns Unit

        useCase(updatedScribble)

        val captured = slot.captured
        assertTrue(captured.rawText == "new text")
        assertTrue(captured.status == ScribbleStatus.COMPLETED)
    }
}
