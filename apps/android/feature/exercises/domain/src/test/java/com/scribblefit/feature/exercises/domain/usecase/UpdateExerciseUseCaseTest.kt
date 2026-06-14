package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateExerciseUseCaseTest {

    private val repository: ExerciseRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: UpdateExerciseUseCase

    private val exercise = Exercise(
        id = 1L, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = listOf(Set(0, 1, 10, 100f)), createdAt = 1_000_000L
    )

    @Before
    fun setup() {
        useCase = UpdateExerciseUseCase(
            repository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success when repository updates successfully`() = runTest(testDispatcher) {
        val result = useCase(exercise)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke calls repository updateExercise with the exercise`() = runTest(testDispatcher) {
        useCase(exercise)

        coVerify(exactly = 1) { repository.updateExercise(exercise) }
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.updateExercise(any()) } throws RuntimeException("DB error")

        val result = useCase(exercise)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `invoke passes updated exercise with changed name to repository`() = runTest(testDispatcher) {
        val updatedExercise = exercise.copy(canonicalName = "Incline Bench Press")

        useCase(updatedExercise)

        coVerify(exactly = 1) { repository.updateExercise(updatedExercise) }
    }

    @Test
    fun `invoke passes updated exercise with new sets to repository`() = runTest(testDispatcher) {
        val newSets = listOf(Set(0, 1, 5, 120f))
        val updatedExercise = exercise.copy(sets = newSets)

        useCase(updatedExercise)

        coVerify(exactly = 1) { repository.updateExercise(updatedExercise) }
    }
}
