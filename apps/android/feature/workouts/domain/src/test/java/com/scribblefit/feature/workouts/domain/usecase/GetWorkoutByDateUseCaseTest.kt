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
import java.util.Date

class GetWorkoutByDateUseCaseTest {

    private val repository = mockk<WorkoutRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = GetWorkoutByDateUseCase(repository, testDispatcher)

    @Test
    fun `should return workout from repository for given date`() = runTest(testDispatcher) {
        // Given
        val date = Date(123456789L)
        val workout = Workout(1L, 123456789L, emptyList())
        every { repository.getWorkoutByDate(date.time) } returns flowOf(workout)

        // When
        val result = useCase(date)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(workout, result.getOrNull())
    }
}
