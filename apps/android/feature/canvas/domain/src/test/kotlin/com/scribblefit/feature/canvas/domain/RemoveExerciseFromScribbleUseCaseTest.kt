package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoveExerciseFromScribbleUseCaseTest {

    private val exerciseRepository: ExerciseRepository = mockk(relaxed = true)
    private val removeScribbleUseCase: RemoveScribbleUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: RemoveExerciseFromScribbleUseCase

    private val exercise = Exercise(
        id = 1L, scribbleId = 10L, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = listOf(Set(0, 1, 10, 100f)), createdAt = 1_000_000L
    )

    @Before
    fun setup() {
        useCase = RemoveExerciseFromScribbleUseCase(
            exerciseRepository = exerciseRepository,
            removeScribbleUseCase = removeScribbleUseCase,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success when exercise is deleted and scribble has remaining exercises`() = runTest(testDispatcher) {
        coEvery { exerciseRepository.getExercisesForScribble(10L) } returns listOf(exercise.copy(id = 2L))

        val result = useCase(1L, 10L)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke calls deleteExercise with correct id`() = runTest(testDispatcher) {
        coEvery { exerciseRepository.getExercisesForScribble(10L) } returns listOf(exercise.copy(id = 2L))

        useCase(1L, 10L)

        coVerify(exactly = 1) { exerciseRepository.deleteExercise(1L) }
    }

    @Test
    fun `invoke removes scribble when no exercises remain`() = runTest(testDispatcher) {
        coEvery { exerciseRepository.getExercisesForScribble(10L) } returns emptyList()

        useCase(1L, 10L)

        coVerify(exactly = 1) { removeScribbleUseCase(10L) }
    }

    @Test
    fun `invoke does NOT remove scribble when other exercises remain`() = runTest(testDispatcher) {
        coEvery { exerciseRepository.getExercisesForScribble(10L) } returns listOf(exercise.copy(id = 2L))

        useCase(1L, 10L)

        coVerify(exactly = 0) { removeScribbleUseCase(any()) }
    }

    @Test
    fun `invoke returns failure when deleteExercise throws`() = runTest(testDispatcher) {
        coEvery { exerciseRepository.deleteExercise(any()) } throws RuntimeException("DB error")

        val result = useCase(1L, 10L)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke does not check remaining exercises when deleteExercise fails`() = runTest(testDispatcher) {
        coEvery { exerciseRepository.deleteExercise(any()) } throws RuntimeException("DB error")

        useCase(1L, 10L)

        coVerify(exactly = 0) { exerciseRepository.getExercisesForScribble(any()) }
    }
}
