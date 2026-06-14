package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Set
import com.scribblefit.feature.scribble.domain.ScribbleRepository
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
import java.time.LocalDateTime

class CreateManualScribbleUseCaseTest {

    private val repository: ScribbleRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: CreateManualScribbleUseCase

    private val testSets = listOf(
        Set(id = 0, setNumber = 1, reps = 10, weight = 100f),
        Set(id = 0, setNumber = 2, reps = 8, weight = 100f)
    )
    private val testDate = CurrentDate(LocalDateTime.of(2024, 3, 1, 9, 0))

    @Before
    fun setup() {
        useCase = CreateManualScribbleUseCase(
            scribbleRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success with generated id on happy path`() = runTest(testDispatcher) {
        coEvery { repository.insertScribble(any()) } returns 42L

        val result = useCase("Bench Press", "Chest", testSets, testDate)

        assertTrue(result.isSuccess)
        assertEquals(42L, result.getOrThrow())
    }

    @Test
    fun `invoke inserts scribble with SUCCESS status`() = runTest(testDispatcher) {
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 1L

        useCase("Bench Press", "Chest", testSets, testDate)

        assertEquals(ScribbleStatus.SUCCESS, slot.captured.status)
    }

    @Test
    fun `invoke inserts scribble with correct exercise name in rawText`() = runTest(testDispatcher) {
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 1L

        useCase("Deadlift", "Back", testSets, testDate)

        assertTrue(slot.captured.rawText.contains("Deadlift"))
    }

    @Test
    fun `invoke inserts scribble with correct date`() = runTest(testDispatcher) {
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 1L

        useCase("Squat", "Legs", testSets, testDate)

        assertEquals(testDate.millis, slot.captured.createdAt)
    }

    @Test
    fun `invoke inserts scribble with one exercise containing the provided sets`() = runTest(testDispatcher) {
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 1L

        useCase("Bench Press", "Chest", testSets, testDate)

        assertEquals(1, slot.captured.exercises.size)
        assertEquals(testSets, slot.captured.exercises[0].sets)
    }

    @Test
    fun `invoke inserts exercise with correct muscle group`() = runTest(testDispatcher) {
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 1L

        useCase("Pull-ups", "Back", testSets, testDate)

        assertEquals("Back", slot.captured.exercises[0].muscleGroup)
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.insertScribble(any()) } throws RuntimeException("DB failure")

        val result = useCase("Bench Press", "Chest", testSets, testDate)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `invoke works with empty sets list`() = runTest(testDispatcher) {
        val slot = slot<Scribble>()
        coEvery { repository.insertScribble(capture(slot)) } returns 1L

        val result = useCase("Bench Press", "Chest", emptyList(), testDate)

        assertTrue(result.isSuccess)
        assertTrue(slot.captured.exercises[0].sets.isEmpty())
    }
}
