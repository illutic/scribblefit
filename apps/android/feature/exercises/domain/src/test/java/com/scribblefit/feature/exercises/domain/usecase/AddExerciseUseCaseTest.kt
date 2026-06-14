package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class AddExerciseUseCaseTest {

    private val repository: ExerciseRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: AddExerciseUseCase

    private val testDate = CurrentDate(LocalDateTime.of(2024, 3, 1, 10, 0))
    private val testSets = listOf(
        Set(id = 0, setNumber = 1, reps = 10, weight = 80f),
        Set(id = 0, setNumber = 2, reps = 8, weight = 80f)
    )

    @Before
    fun setup() {
        useCase = AddExerciseUseCase(
            exerciseRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success on happy path`() = runTest(testDispatcher) {
        val result = useCase(testDate, 1L, "Bench Press", "Chest", testSets)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke calls addExercisesWithSets on repository`() = runTest(testDispatcher) {
        useCase(testDate, 1L, "Bench Press", "Chest", testSets)

        coVerify(exactly = 1) { repository.addExercisesWithSets(1L, any()) }
    }

    @Test
    fun `invoke constructs exercise with correct name and muscle group`() = runTest(testDispatcher) {
        val listSlot = slot<List<Exercise>>()
        coEvery { repository.addExercisesWithSets(any(), capture(listSlot)) } returns Unit

        useCase(testDate, 5L, "Deadlift", "Back", testSets)

        val capturedExercise = listSlot.captured[0]
        assertTrue(capturedExercise.canonicalName == "Deadlift")
        assertTrue(capturedExercise.muscleGroup == "Back")
    }

    @Test
    fun `invoke constructs exercise with correct date millis`() = runTest(testDispatcher) {
        val listSlot = slot<List<Exercise>>()
        coEvery { repository.addExercisesWithSets(any(), capture(listSlot)) } returns Unit

        useCase(testDate, 1L, "Squat", "Legs", testSets)

        val capturedExercise = listSlot.captured[0]
        assertTrue(capturedExercise.createdAt == testDate.millis)
    }

    @Test
    fun `invoke constructs exercise with the provided sets`() = runTest(testDispatcher) {
        val listSlot = slot<List<Exercise>>()
        coEvery { repository.addExercisesWithSets(any(), capture(listSlot)) } returns Unit

        useCase(testDate, 1L, "Bench Press", "Chest", testSets)

        val capturedExercise = listSlot.captured[0]
        assertTrue(capturedExercise.sets == testSets)
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.addExercisesWithSets(any(), any()) } throws RuntimeException("DB error")

        val result = useCase(testDate, 1L, "Bench Press", "Chest", testSets)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke inserts exactly one exercise into repository`() = runTest(testDispatcher) {
        val listSlot = slot<List<Exercise>>()
        coEvery { repository.addExercisesWithSets(any(), capture(listSlot)) } returns Unit

        useCase(testDate, 1L, "Bench Press", "Chest", testSets)

        assertTrue(listSlot.captured.size == 1)
    }
}
