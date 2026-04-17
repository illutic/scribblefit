package com.scribblefit.feature.ledger.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Workout
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class GetWorkoutsInRangeUseCaseTest {

    private val repository = mockk<WorkoutRepository>()
    private val useCase = GetWorkoutsInRangeUseCase(repository)

    @Test
    fun `should request workouts from repository and filter out empty ones`() = runTest {
        // Given
        val startDate = LocalDate.of(2026, 3, 1)
        val endDate = LocalDate.of(2026, 3, 31)
        
        val startMillis = startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endMillis = endDate.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli()
        
        val validExercise = Exercise(id = 1L, canonicalName = "Test", muscleGroup = "Test", sets = emptyList(), isDraft = false)
        val emptyWorkout = Workout(id = 1L, date = startMillis, exercises = emptyList())
        val validWorkout = Workout(id = 2L, date = startMillis, exercises = listOf(validExercise))
        
        val expectedWorkouts = listOf(validWorkout)
        
        every { repository.getWorkoutsInRange(startMillis, endMillis) } returns flowOf(listOf(emptyWorkout, validWorkout))

        // When
        val result = useCase(startDate, endDate).first()

        // Then
        assertEquals(expectedWorkouts, result)
        verify { repository.getWorkoutsInRange(startMillis, endMillis) }
    }
}
