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

class UpdateExerciseUseCaseTest {

    private val repository = mockk<ExerciseRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = UpdateExerciseUseCase(repository, testDispatcher)

    @Test
    fun `should call updateExercise on repository`() = runTest(testDispatcher) {
        // Given
        val exercise = Exercise(
            id = 1L,
            canonicalName = "Bench Press",
            muscleGroup = "Chest",
            sets = emptyList(),
            createdAt = 0L
        )
        coEvery { repository.updateExercise(exercise) } returns Unit

        // When
        val result = useCase(exercise)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.updateExercise(exercise) }
    }
}
