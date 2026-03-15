package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.exercises.domain.usecase.MarkExerciseAsCompleteUseCase
import com.scribblefit.feature.scribble.domain.ScribbleNotFoundException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateScribbleAsCompleteUseCaseTest {

    private val scribbleRepository = mockk<ScribbleRepository>()
    private val markExerciseAsCompleteUseCase = mockk<MarkExerciseAsCompleteUseCase>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = UpdateScribbleAsCompleteUseCase(
        scribbleRepository,
        markExerciseAsCompleteUseCase,
        testDispatcher
    )

    @Test
    fun `when called, should update all exercises to non-draft and mark scribble as complete`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        val exercise1 = Exercise(201L, "Bench Press", "Chest", emptyList(), isDraft = true)
        val exercise2 = Exercise(202L, "Squat", "Legs", emptyList(), isDraft = true)
        val scribble = Scribble(
            id = scribbleId,
            rawText = "raw text",
            parsedJson = null,
            status = ScribbleStatus.PARSED,
            createdAt = 123456789L,
            exercises = listOf(exercise1, exercise2)
        )

        every { scribbleRepository.getScribbleWithExercises(scribbleId) } returns flowOf(scribble)
        coEvery { markExerciseAsCompleteUseCase(any()) } returns Result.success(Unit)
        coEvery { scribbleRepository.updateScribble(any()) } returns Unit

        // When
        val result = useCase.invoke(scribbleId)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            markExerciseAsCompleteUseCase(exercise1)
            markExerciseAsCompleteUseCase(exercise2)
            scribbleRepository.updateScribble(match {
                it.id == scribbleId && it.status == ScribbleStatus.COMPLETED
            })
        }
    }

    @Test
    fun `when scribble not found, should return failure`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        every { scribbleRepository.getScribbleWithExercises(scribbleId) } returns flowOf()

        // When
        val result = useCase.invoke(scribbleId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ScribbleNotFoundException)
    }
}
