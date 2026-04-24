package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoveExerciseUseCaseTest {

    private val repository = mockk<ExerciseRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = RemoveExerciseUseCase(repository, testDispatcher)

    @Test
    fun `should call deleteExercise on repository`() = runTest(testDispatcher) {
        // Given
        val exerciseId = 1L
        coEvery { repository.deleteExercise(exerciseId) } returns Unit

        // When
        val result = useCase(exerciseId)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.deleteExercise(exerciseId) }
    }
}
