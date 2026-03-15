package com.scribblefit.feature.workouts.domain.usecase

import com.scribblefit.core.model.Workout
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetWorkoutUseCaseTest {

    private val repository = mockk<WorkoutRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = GetWorkoutUseCase(repository, testDispatcher)

    @Test
    fun `should return workout from repository for given id`() = runTest(testDispatcher) {
        // Given
        val workoutId = 1L
        val workout = Workout(workoutId, 123456789L, emptyList())
        every { repository.getWorkoutById(workoutId) } returns flowOf(workout)

        // When
        val result = useCase(workoutId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(workout, result.getOrNull())
    }
}
