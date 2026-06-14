package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class AddExercisesUseCaseTest {

    private val repository: ExerciseRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: AddExercisesUseCase

    private val testDate = CurrentDate(LocalDateTime.of(2024, 3, 1, 10, 0))
    private val exercise1 = Exercise(
        id = 0, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = listOf(Set(0, 1, 10, 100f)), createdAt = 0L
    )
    private val exercise2 = Exercise(
        id = 0, canonicalName = "Squat", muscleGroup = "Legs",
        sets = listOf(Set(0, 1, 5, 120f)), createdAt = 0L
    )

    @Before
    fun setup() {
        useCase = AddExercisesUseCase(
            exerciseRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success on happy path`() = runTest(testDispatcher) {
        val result = useCase(testDate, 1L, listOf(exercise1, exercise2))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke stamps all exercises with provided date millis`() = runTest(testDispatcher) {
        val listSlot = slot<List<Exercise>>()
        coEvery { repository.addExercisesWithSets(any(), capture(listSlot)) } returns Unit

        useCase(testDate, 1L, listOf(exercise1, exercise2))

        listSlot.captured.forEach { exercise ->
            assertEquals(testDate.millis, exercise.createdAt)
        }
    }

    @Test
    fun `invoke preserves exercise names and muscle groups`() = runTest(testDispatcher) {
        val listSlot = slot<List<Exercise>>()
        coEvery { repository.addExercisesWithSets(any(), capture(listSlot)) } returns Unit

        useCase(testDate, 1L, listOf(exercise1, exercise2))

        assertEquals("Bench Press", listSlot.captured[0].canonicalName)
        assertEquals("Squat", listSlot.captured[1].canonicalName)
    }

    @Test
    fun `invoke passes correct scribble id to repository`() = runTest(testDispatcher) {
        useCase(testDate, 42L, listOf(exercise1))

        coVerify(exactly = 1) { repository.addExercisesWithSets(42L, any()) }
    }

    @Test
    fun `invoke returns success with empty exercise list`() = runTest(testDispatcher) {
        val result = useCase(testDate, 1L, emptyList())

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.addExercisesWithSets(1L, emptyList()) }
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.addExercisesWithSets(any(), any()) } throws RuntimeException("DB error")

        val result = useCase(testDate, 1L, listOf(exercise1))

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke preserves sets within each exercise`() = runTest(testDispatcher) {
        val listSlot = slot<List<Exercise>>()
        coEvery { repository.addExercisesWithSets(any(), capture(listSlot)) } returns Unit

        useCase(testDate, 1L, listOf(exercise1))

        assertEquals(exercise1.sets, listSlot.captured[0].sets)
    }
}
