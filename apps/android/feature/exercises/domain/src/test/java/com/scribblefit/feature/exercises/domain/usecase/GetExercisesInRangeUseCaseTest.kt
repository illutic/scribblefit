package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetExercisesInRangeUseCaseTest {

    private val repository: ExerciseRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetExercisesInRangeUseCase

    private val startDate = CurrentDate(LocalDateTime.of(2024, 1, 1, 0, 0))
    private val endDate = CurrentDate(LocalDateTime.of(2024, 1, 31, 23, 59))

    private fun exercise(id: Long) = Exercise(
        id = id, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = listOf(Set(0, 1, 10, 100f)), createdAt = startDate.millis + id
    )

    @Before
    fun setup() {
        useCase = GetExercisesInRangeUseCase(
            exerciseRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success with exercises on happy path`() = runTest(testDispatcher) {
        val exercises = listOf(exercise(1L), exercise(2L))
        coEvery { repository.getExercisesInRange(startDate.millis, endDate.millis) } returns exercises

        val result = useCase(startDate, endDate)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
    }

    @Test
    fun `invoke passes correct date millis to repository`() = runTest(testDispatcher) {
        coEvery { repository.getExercisesInRange(any(), any()) } returns emptyList()

        useCase(startDate, endDate)

        coVerify(exactly = 1) { repository.getExercisesInRange(startDate.millis, endDate.millis) }
    }

    @Test
    fun `invoke returns empty list when no exercises in range`() = runTest(testDispatcher) {
        coEvery { repository.getExercisesInRange(any(), any()) } returns emptyList()

        val result = useCase(startDate, endDate)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.getExercisesInRange(any(), any()) } throws RuntimeException("DB error")

        val result = useCase(startDate, endDate)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke returns all exercises without filtering`() = runTest(testDispatcher) {
        val exercises = listOf(exercise(1L), exercise(2L), exercise(3L))
        coEvery { repository.getExercisesInRange(startDate.millis, endDate.millis) } returns exercises

        val result = useCase(startDate, endDate)

        assertEquals(3, result.getOrThrow().size)
    }
}
