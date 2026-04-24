package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.feature.exercises.domain.usecase.RemoveExerciseUseCase
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
    fun `when invoked, should clear scribble exercises and delete the scribble`() =
        runTest(testDispatcher) {
            // Given
            val scribbleId = 1L
            coEvery { scribbleRepository.clearScribbleExercises(scribbleId) } returns Unit
            coEvery { scribbleRepository.deleteScribble(scribbleId) } returns Unit

            // When
            val result = useCase(scribbleId)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) {
                scribbleRepository.clearScribbleExercises(scribbleId)
                scribbleRepository.deleteScribble(scribbleId)
            }
        }

    @Test
    fun `when clearScribbleExercises fails, should return failure`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        val exception = RuntimeException("Clear failed")
        coEvery { scribbleRepository.clearScribbleExercises(scribbleId) } throws exception

        // When
        val result = useCase(scribbleId)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { scribbleRepository.deleteScribble(any()) }
    }
}
