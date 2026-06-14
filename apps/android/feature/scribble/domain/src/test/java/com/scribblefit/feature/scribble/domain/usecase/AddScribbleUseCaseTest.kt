package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class AddScribbleUseCaseTest {

    private val repository: ScribbleRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: AddScribbleUseCase

    @Before
    fun setup() {
        useCase = AddScribbleUseCase(repository = repository)
    }

    @Test
    fun `invoke inserts scribble with PENDING status`() = runTest(testDispatcher) {
        val date = CurrentDate(LocalDateTime.of(2024, 1, 15, 10, 0))
        val rawText = "bench press 3x10 @ 100kg"
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 1L

        useCase(rawText, date)

        coVerify(exactly = 1) { repository.insertScribble(any()) }
        assertEquals(ScribbleStatus.PENDING, slot.captured.status)
    }

    @Test
    fun `invoke inserts scribble with correct raw text`() = runTest(testDispatcher) {
        val date = CurrentDate(LocalDateTime.of(2024, 1, 15, 10, 0))
        val rawText = "squat 5x5 @ 80kg"
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 1L

        useCase(rawText, date)

        assertEquals(rawText, slot.captured.rawText)
    }

    @Test
    fun `invoke inserts scribble with correct date in millis`() = runTest(testDispatcher) {
        val date = CurrentDate(LocalDateTime.of(2024, 1, 15, 10, 0))
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 1L

        useCase("text", date)

        assertEquals(date.millis, slot.captured.createdAt)
    }

    @Test
    fun `invoke always inserts scribble with id 0 for DB generation`() = runTest(testDispatcher) {
        val date = CurrentDate(LocalDateTime.now())
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 99L

        useCase("any text", date)

        assertEquals(0L, slot.captured.id)
    }

    @Test
    fun `invoke with empty text still inserts scribble`() = runTest(testDispatcher) {
        val date = CurrentDate(LocalDateTime.now())
        coEvery { repository.insertScribble(any()) } returns 1L

        useCase("", date)

        coVerify(exactly = 1) { repository.insertScribble(any()) }
    }
}
