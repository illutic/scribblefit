package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkExerciseAsCompleteUseCaseTest {

    private val updateExerciseUseCase = mockk<UpdateExerciseUseCase>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = MarkExerciseAsCompleteUseCase(updateExerciseUseCase, testDispatcher)

    @Test
    fun `should call updateExerciseUseCase with isDraft false`() = runTest(testDispatcher) {
        // Given
        val exercise = Exercise(
            id = 1L,
            canonicalName = "Bench Press",
            muscleGroup = "Chest",
            sets = emptyList(),
            createdAt = 0L,
            isDraft = true
        )
        coEvery { updateExerciseUseCase(exercise.copy(isDraft = false)) } returns Result.success(
            Unit
        )

        // When
        val result = useCase(exercise)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { updateExerciseUseCase(exercise.copy(isDraft = false)) }
    }
}
