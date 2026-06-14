package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoveExerciseUseCaseTest {

    private val repository: ExerciseRepository = mockk()
    private val scribbleRepository: ScribbleRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: RemoveExerciseUseCase

    private val exercise = Exercise(
        id = 1L, scribbleId = 10L, canonicalName = "Bench Press",
        muscleGroup = "Chest", sets = listOf(Set(0, 1, 10, 100f)), createdAt = 1_000_000L
    )

    @Before
    fun setup() {
        useCase = RemoveExerciseUseCase(
            repository = repository,
            scribbleRepository = scribbleRepository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success when exercise exists and deletion succeeds`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(1L) } returns exercise
        coEvery { repository.deleteExercise(1L) } returns Unit
        coEvery { repository.getExercisesForScribble(10L) } returns listOf(
            exercise.copy(id = 2L) // still has other exercises
        )

        val result = useCase(1L)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke returns success silently when exercise not found`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(999L) } returns null

        val result = useCase(999L)

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { repository.deleteExercise(any()) }
    }

    @Test
    fun `invoke deletes scribble when it was the last exercise`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(1L) } returns exercise
        coEvery { repository.deleteExercise(1L) } returns Unit
        coEvery { repository.getExercisesForScribble(10L) } returns emptyList()

        useCase(1L)

        coVerify(exactly = 1) { scribbleRepository.deleteScribble(10L) }
    }

    @Test
    fun `invoke does NOT delete scribble when other exercises remain`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(1L) } returns exercise
        coEvery { repository.deleteExercise(1L) } returns Unit
        coEvery { repository.getExercisesForScribble(10L) } returns listOf(exercise.copy(id = 2L))

        useCase(1L)

        coVerify(exactly = 0) { scribbleRepository.deleteScribble(any()) }
    }

    @Test
    fun `invoke returns failure when deleteExercise throws`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(1L) } returns exercise
        coEvery { repository.deleteExercise(1L) } throws RuntimeException("DB error")

        val result = useCase(1L)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke calls deleteExercise with correct id`() = runTest(testDispatcher) {
        coEvery { repository.getExerciseById(1L) } returns exercise
        coEvery { repository.deleteExercise(1L) } returns Unit
        coEvery { repository.getExercisesForScribble(10L) } returns emptyList()

        useCase(1L)

        coVerify(exactly = 1) { repository.deleteExercise(1L) }
    }
}
