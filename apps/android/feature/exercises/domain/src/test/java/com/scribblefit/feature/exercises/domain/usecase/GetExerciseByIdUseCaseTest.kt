package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetExerciseByIdUseCaseTest {

    private val repository: ExerciseRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetExerciseByIdUseCase

    private val testExercise = Exercise(
        id = 1L, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = listOf(Set(0, 1, 10, 100f)), createdAt = 1_000_000L
    )

    @Before
    fun setup() {
        useCase = GetExerciseByIdUseCase(
            exerciseRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success with exercise when found`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(1L) } returns testExercise

        val result = useCase(1L)

        assertTrue(result.isSuccess)
        assertEquals(testExercise, result.getOrThrow())
    }

    @Test
    fun `invoke returns failure when exercise not found`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(999L) } returns null

        val result = useCase(999L)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `invoke returns failure with correct message when not found`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(42L) } returns null

        val result = useCase(42L)

        assertTrue(result.exceptionOrNull()?.message?.contains("not found") == true)
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(any()) } throws RuntimeException("DB error")

        val result = useCase(1L)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke calls repository with correct id`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(5L) } returns testExercise.copy(id = 5L)

        val result = useCase(5L)

        assertTrue(result.isSuccess)
        assertEquals(5L, result.getOrThrow().id)
    }
}
