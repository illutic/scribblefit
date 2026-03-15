package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.core.model.Set
import com.scribblefit.feature.sets.domain.SetNumberNotValidException
import com.scribblefit.feature.sets.domain.SetRepository
import com.scribblefit.feature.sets.domain.SetRepsNotValidException
import com.scribblefit.feature.sets.domain.SetWeightNotValidException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InsertSetToExerciseUseCaseTest {

    private val repository = mockk<SetRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = InsertSetToExerciseUseCase(repository, testDispatcher)

    @Test
    fun `when set is valid, should call repository and return success`() = runTest(testDispatcher) {
        // Given
        val workoutExerciseId = 1L
        val set = Set(0L, 1, 100f, 10)
        coEvery { repository.addSet(workoutExerciseId, set) } returns 101L

        // When
        val result = useCase(workoutExerciseId, set)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(101L, result.getOrNull())
        coVerify(exactly = 1) { repository.addSet(workoutExerciseId, set) }
    }

    @Test
    fun `when reps are not valid, should return failure`() = runTest(testDispatcher) {
        // Given
        val set = Set(0L, 1, 100f, 0)

        // When
        val result = useCase(1L, set)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is SetRepsNotValidException)
    }

    @Test
    fun `when weight is not valid, should return failure`() = runTest(testDispatcher) {
        // Given
        val set = Set(0L, 1, -10f, 10)

        // When
        val result = useCase(1L, set)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is SetWeightNotValidException)
    }

    @Test
    fun `when set number is not valid, should return failure`() = runTest(testDispatcher) {
        // Given
        val set = Set(0L, 0, 100f, 10)

        // When
        val result = useCase(1L, set)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is SetNumberNotValidException)
    }
}
