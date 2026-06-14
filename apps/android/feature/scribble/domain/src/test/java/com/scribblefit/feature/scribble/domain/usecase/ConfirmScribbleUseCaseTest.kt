package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.error.ScribbleError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConfirmScribbleUseCaseTest {

    private val repository: ScribbleRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: ConfirmScribbleUseCase

    private fun scribble(status: ScribbleStatus) = Scribble(
        id = 1L,
        rawText = "bench press 3x10",
        status = status,
        createdAt = 1_000_000L
    )

    @Before
    fun setup() {
        useCase = ConfirmScribbleUseCase(
            scribbleRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success when scribble status is SUCCESS`() = runTest(testDispatcher) {
        val scribble = scribble(ScribbleStatus.SUCCESS)

        val result = useCase(scribble)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke updates scribble status to COMPLETED on success`() = runTest(testDispatcher) {
        val scribble = scribble(ScribbleStatus.SUCCESS)
        val slot = slot<Scribble>()
        coEvery { repository.updateScribble(capture(slot)) } returns Unit

        useCase(scribble)

        coVerify(exactly = 1) { repository.updateScribble(any()) }
        assertEquals(ScribbleStatus.COMPLETED, slot.captured.status)
    }

    @Test
    fun `invoke returns failure with InvalidStatus when status is PENDING`() = runTest(testDispatcher) {
        val scribble = scribble(ScribbleStatus.PENDING)

        val result = useCase(scribble)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is ScribbleError.InvalidStatus)
    }

    @Test
    fun `invoke returns failure with InvalidStatus when status is PARSING`() = runTest(testDispatcher) {
        val scribble = scribble(ScribbleStatus.PARSING)

        val result = useCase(scribble)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is ScribbleError.InvalidStatus)
    }

    @Test
    fun `invoke returns failure with InvalidStatus when status is FAILED`() = runTest(testDispatcher) {
        val scribble = scribble(ScribbleStatus.FAILED)

        val result = useCase(scribble)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke returns failure when status is already COMPLETED`() = runTest(testDispatcher) {
        val scribble = scribble(ScribbleStatus.COMPLETED)

        val result = useCase(scribble)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke does not call repository when status check fails`() = runTest(testDispatcher) {
        val scribble = scribble(ScribbleStatus.PENDING)

        useCase(scribble)

        coVerify(exactly = 0) { repository.updateScribble(any()) }
    }

    @Test
    fun `invoke wraps repository exception as failure`() = runTest(testDispatcher) {
        val scribble = scribble(ScribbleStatus.SUCCESS)
        coEvery { repository.updateScribble(any()) } throws RuntimeException("DB error")

        val result = useCase(scribble)

        assertFalse(result.isSuccess)
    }
}
