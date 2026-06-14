package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.sets.domain.SetRepository
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

class AddSetToExerciseUseCaseTest {

    private val repository: SetRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: AddSetToExerciseUseCase

    private fun set(id: Long, setNumber: Int, reps: Int = 10, weight: Float = 80f) =
        Set(id = id, setNumber = setNumber, reps = reps, weight = weight)

    private fun exercise(sets: List<Set>) = Exercise(
        id = 1L, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = sets, createdAt = 1_000_000L
    )

    @Before
    fun setup() {
        useCase = AddSetToExerciseUseCase(
            repository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success with new set id on happy path`() = runTest(testDispatcher) {
        coEvery { repository.addSet(any(), any()) } returns 10L
        val exercise = exercise(listOf(set(1L, 1), set(2L, 2)))

        val result = useCase(exercise)

        assertTrue(result.isSuccess)
        assertEquals(10L, result.getOrThrow())
    }

    @Test
    fun `invoke creates new set with setNumber = max + 1`() = runTest(testDispatcher) {
        val setSlot = slot<Set>()
        coEvery { repository.addSet(any(), capture(setSlot)) } returns 1L
        val exercise = exercise(listOf(set(1L, 1), set(2L, 2), set(3L, 3)))

        useCase(exercise)

        assertEquals(4, setSlot.captured.setNumber)
    }

    @Test
    fun `invoke creates first set with setNumber 1 when exercise has no sets`() = runTest(testDispatcher) {
        val setSlot = slot<Set>()
        coEvery { repository.addSet(any(), capture(setSlot)) } returns 1L
        val exercise = exercise(emptyList())

        useCase(exercise)

        assertEquals(1, setSlot.captured.setNumber)
    }

    @Test
    fun `invoke creates new set with zero reps and zero weight`() = runTest(testDispatcher) {
        val setSlot = slot<Set>()
        coEvery { repository.addSet(any(), capture(setSlot)) } returns 1L
        val exercise = exercise(listOf(set(1L, 1)))

        useCase(exercise)

        assertEquals(0, setSlot.captured.reps)
        assertEquals(0f, setSlot.captured.weight)
    }

    @Test
    fun `invoke creates new set with id 0 for DB generation`() = runTest(testDispatcher) {
        val setSlot = slot<Set>()
        coEvery { repository.addSet(any(), capture(setSlot)) } returns 1L
        val exercise = exercise(emptyList())

        useCase(exercise)

        assertEquals(0L, setSlot.captured.id)
    }

    @Test
    fun `invoke calls repository with correct exercise id`() = runTest(testDispatcher) {
        coEvery { repository.addSet(any(), any()) } returns 1L
        val exercise = exercise(emptyList())

        useCase(exercise)

        coVerify(exactly = 1) { repository.addSet(1L, any()) }
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.addSet(any(), any()) } throws RuntimeException("DB error")
        val exercise = exercise(listOf(set(1L, 1)))

        val result = useCase(exercise)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke handles non-sequential setNumbers and uses max + 1`() = runTest(testDispatcher) {
        val setSlot = slot<Set>()
        coEvery { repository.addSet(any(), capture(setSlot)) } returns 1L
        // Sets with non-sequential numbers: 1, 5, 3
        val exercise = exercise(listOf(set(1L, 1), set(2L, 5), set(3L, 3)))

        useCase(exercise)

        assertEquals(6, setSlot.captured.setNumber) // max is 5, so next is 6
    }
}
