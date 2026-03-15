package com.scribblefit.feature.workouts.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.exercises.domain.usecase.InsertExerciseToWorkoutUseCase
import com.scribblefit.feature.workouts.domain.InvalidWorkoutDateException
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InsertWorkoutUseCaseTest {

    private val repository = mockk<WorkoutRepository>()
    private val insertExerciseToWorkoutUseCase = mockk<InsertExerciseToWorkoutUseCase>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = InsertWorkoutUseCase(
        repository,
        insertExerciseToWorkoutUseCase,
        testDispatcher
    )

    @Test
    fun `when workout is valid, should save workout and exercises`() = runTest(testDispatcher) {
        // Given
        val exercise1 = Exercise(0L, "Bench Press", "Chest", emptyList())
        val workout = Workout(0L, 123456789L, listOf(exercise1))
        val workoutId = 100L

        coEvery { repository.saveWorkout(workout) } returns workoutId
        coEvery { insertExerciseToWorkoutUseCase(workoutId, exercise1) } returns Result.success(201L)

        // When
        val result = useCase(workout)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(workoutId, result.getOrNull())
        coVerify {
            repository.saveWorkout(workout)
            insertExerciseToWorkoutUseCase(workoutId, exercise1)
        }
    }

    @Test
    fun `when workout date is invalid, should return failure`() = runTest(testDispatcher) {
        // Given
        val workout = Workout(0L, -1L, emptyList())

        // When
        val result = useCase(workout)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidWorkoutDateException)
    }
}
