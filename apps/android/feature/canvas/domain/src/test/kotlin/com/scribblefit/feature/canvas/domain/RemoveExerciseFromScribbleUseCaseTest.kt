package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RemoveExerciseFromScribbleUseCaseTest {

    private val exerciseRepository = mockk<ExerciseRepository>()
    private val removeScribbleUseCase = mockk<RemoveScribbleUseCase>()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val useCase = RemoveExerciseFromScribbleUseCase(
        exerciseRepository = exerciseRepository,
        removeScribbleUseCase = removeScribbleUseCase,
        coroutineDispatcher = testDispatcher
    )

    @Test
    fun `deletes exercise and scribble when it was the last exercise`() = runTest(testDispatcher) {
        val exerciseId = 10L
        val scribbleId = 1L

        coEvery { exerciseRepository.deleteExercise(any()) } returns Unit
        coEvery { exerciseRepository.getExercisesForScribble(any()) } returns emptyList()
        coEvery { removeScribbleUseCase(any()) } returns Result.success(Unit)

        useCase(exerciseId, scribbleId)

        coVerify { exerciseRepository.deleteExercise(exerciseId) }
        coVerify { removeScribbleUseCase(scribbleId) }
    }

    @Test
    fun `deletes exercise but not scribble when other exercises remain`() = runTest(testDispatcher) {
        val exerciseId = 10L
        val scribbleId = 1L
        val remainingExercise = Exercise(
            id = 11L,
            canonicalName = "Bench",
            muscleGroup = "Chest",
            sets = emptyList(),
            createdAt = 0L
        )

        coEvery { exerciseRepository.deleteExercise(any()) } returns Unit
        coEvery { exerciseRepository.getExercisesForScribble(any()) } returns listOf(remainingExercise)

        useCase(exerciseId, scribbleId)

        coVerify { exerciseRepository.deleteExercise(exerciseId) }
        coVerify(exactly = 0) { removeScribbleUseCase(any()) }
    }
}
