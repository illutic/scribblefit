package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.exercises.domain.usecase.RemoveExerciseUseCase
import com.scribblefit.feature.scribble.domain.ScribbleNotFoundException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoveScribbleUseCaseTest {

    private val scribbleRepository = mockk<ScribbleRepository>()
    private val removeExerciseUseCase = mockk<RemoveExerciseUseCase>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = RemoveScribbleUseCase(
        scribbleRepository,
        removeExerciseUseCase,
        testDispatcher
    )

    @Test
    fun `when scribble exists, should delete its exercises and the scribble itself`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        val exerciseId1 = 101L
        val exerciseId2 = 102L
        val exercise1 = mockk<Exercise> { every { id } returns exerciseId1 }
        val exercise2 = mockk<Exercise> { every { id } returns exerciseId2 }
        val scribble = mockk<Scribble> {
            every { id } returns scribbleId
            every { exercises } returns listOf(exercise1, exercise2)
        }

        every { scribbleRepository.getScribbleWithExercises(scribbleId) } returns flowOf(scribble)
        coEvery { removeExerciseUseCase(any()) } returns Result.success(Unit)
        coEvery { scribbleRepository.deleteScribble(scribbleId) } returns Unit

        // When
        val result = useCase(scribbleId)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            removeExerciseUseCase(exercise1)
            removeExerciseUseCase(exercise2)
            scribbleRepository.deleteScribble(scribbleId)
        }
    }

    @Test
    fun `when scribble does not exist, should return failure with ScribbleNotFoundException`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        every { scribbleRepository.getScribbleWithExercises(scribbleId) } returns flowOf()

        // When
        val result = useCase(scribbleId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ScribbleNotFoundException)
        coVerify(exactly = 0) {
            removeExerciseUseCase(any())
            scribbleRepository.deleteScribble(any())
        }
    }

    @Test
    fun `when exercise deletion fails, should return failure and not delete scribble`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        val exercise1 = mockk<Exercise> { every { id } returns 101L }
        val scribble = mockk<Scribble> {
            every { id } returns scribbleId
            every { exercises } returns listOf(exercise1)
        }
        val exception = RuntimeException("Deletion failed")

        every { scribbleRepository.getScribbleWithExercises(scribbleId) } returns flowOf(scribble)
        coEvery { removeExerciseUseCase(any()) } returns Result.failure(exception)

        // When
        val result = useCase(scribbleId)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { scribbleRepository.deleteScribble(any()) }
    }
}
