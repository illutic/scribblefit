package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.scribble.domain.ScribbleNotFoundException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.workouts.domain.usecase.GetWorkoutUseCase
import com.scribblefit.feature.workouts.domain.usecase.InsertWorkoutUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateScribbleWithWorkoutUseCaseTest {

    private val scribbleRepository = mockk<ScribbleRepository>()
    private val insertWorkoutUseCase = mockk<InsertWorkoutUseCase>()
    private val getWorkoutUseCase = mockk<GetWorkoutUseCase>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = UpdateScribbleWithWorkoutUseCase(
        scribbleRepository,
        insertWorkoutUseCase,
        getWorkoutUseCase,
        testDispatcher
    )

    @Test
    fun `when called, should insert workout, link exercises to scribble, and update status`() =
        runTest(testDispatcher) {
            // Given
            val scribbleId = 1L
            val workoutId = 100L
            val exerciseId1 = 201L
            val exerciseId2 = 202L
            val exercise1 = mockk<Exercise> { every { id } returns exerciseId1 }
            val exercise2 = mockk<Exercise> { every { id } returns exerciseId2 }
            val workout = mockk<Workout> {
                every { exercises } returns listOf(exercise1, exercise2)
            }

            val existingScribble = Scribble(
                id = scribbleId,
                rawText = "raw text",
                parsedJson = null,
                status = ScribbleStatus.RAW,
                createdAt = 123456789L,
                exercises = emptyList()
            )

            coEvery { insertWorkoutUseCase(workout) } returns Result.success(workoutId)
            coEvery { getWorkoutUseCase(workoutId) } returns Result.success(workout)
            coEvery { scribbleRepository.addExerciseToScribble(scribbleId, exerciseId1) } returns 1L
            coEvery { scribbleRepository.addExerciseToScribble(scribbleId, exerciseId2) } returns 2L
            every { scribbleRepository.getScribble(scribbleId) } returns flowOf(existingScribble)
            coEvery { scribbleRepository.updateScribble(any()) } returns Unit

            // When
            val result = useCase(scribbleId, workout)

            // Then
            assertTrue(result.isSuccess)

            coVerify {
                insertWorkoutUseCase(workout)
                getWorkoutUseCase(workoutId)
                scribbleRepository.addExerciseToScribble(scribbleId, exerciseId1)
                scribbleRepository.addExerciseToScribble(scribbleId, exerciseId2)
                scribbleRepository.updateScribble(match {
                    it.id == scribbleId && it.status == ScribbleStatus.PARSED
                })
            }
        }

    @Test
    fun `when insert workout fails, should return failure`() =
        runTest(testDispatcher) {
            // Given
            val scribbleId = 1L
            val workout = mockk<Workout>()
            val exception = RuntimeException("Insert failed")
            coEvery { insertWorkoutUseCase(workout) } returns Result.failure(exception)

            // When
            val result = useCase(scribbleId, workout)

            // Then
            assertTrue(result.isFailure)
            coVerify(exactly = 0) { scribbleRepository.addExerciseToScribble(any(), any()) }
        }

    @Test
    fun `when scribble not found during status update, should return failure`() =
        runTest(testDispatcher) {
            // Given
            val scribbleId = 1L
            val workoutId = 100L
            val workout = mockk<Workout> { every { exercises } returns emptyList() }

            coEvery { insertWorkoutUseCase(workout) } returns Result.success(workoutId)
            coEvery { getWorkoutUseCase(workoutId) } returns Result.success(workout)
            every { scribbleRepository.getScribble(scribbleId) } returns flowOf() // Empty flow

            // When
            val result = useCase(scribbleId, workout)

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is ScribbleNotFoundException)
        }
}
