package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseNameNotValidException
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.exercises.domain.ExerciseSetsNotValidException
import com.scribblefit.feature.sets.domain.usecase.InsertSetToExerciseUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InsertExerciseToWorkoutUseCaseTest {

    private val repository = mockk<ExerciseRepository>()
    private val insertSetToExerciseUseCase = mockk<InsertSetToExerciseUseCase>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = InsertExerciseToWorkoutUseCase(
        repository,
        insertSetToExerciseUseCase,
        testDispatcher
    )

    @Test
    fun `when exercise is valid, should add as draft and return workoutExerciseId`() = runTest(testDispatcher) {
        // Given
        val workoutId = 1L
        val set1 = Set(0L, 1, 100f, 10)
        val exercise = Exercise(0L, "Bench Press", "Chest", listOf(set1))
        val exerciseId = 101L
        val workoutExerciseId = 201L

        coEvery { repository.addExercise(any()) } returns exerciseId
        coEvery { repository.addExerciseToWorkout(workoutId, exerciseId) } returns workoutExerciseId
        coEvery { insertSetToExerciseUseCase(workoutExerciseId, set1) } returns Result.success(301L)

        // When
        val result = useCase(workoutId, exercise)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(workoutExerciseId, result.getOrNull())
        coVerify {
            repository.addExercise(exercise.copy(isDraft = true))
            repository.addExerciseToWorkout(workoutId, exerciseId)
            insertSetToExerciseUseCase(workoutExerciseId, set1)
        }
    }

    @Test
    fun `when exercise name is blank, should return failure`() = runTest(testDispatcher) {
        // Given
        val workoutId = 1L
        val exercise = Exercise(0L, "  ", "Chest", listOf(Set(0L, 1, 100f, 10)))

        // When
        val result = useCase(workoutId, exercise)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ExerciseNameNotValidException)
    }

    @Test
    fun `when exercise has no sets, should return failure`() = runTest(testDispatcher) {
        // Given
        val workoutId = 1L
        val exercise = Exercise(0L, "Bench Press", "Chest", emptyList())

        // When
        val result = useCase(workoutId, exercise)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ExerciseSetsNotValidException)
    }
}
