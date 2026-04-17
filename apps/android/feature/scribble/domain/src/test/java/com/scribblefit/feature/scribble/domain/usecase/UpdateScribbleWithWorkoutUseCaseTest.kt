package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
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

class UpdateScribbleWithWorkoutUseCaseTest {

    private val scribbleRepository = mockk<ScribbleRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = UpdateScribbleWithWorkoutUseCase(
        scribbleRepository,
        testDispatcher
    )

    @Test
    fun `when called, should clear exercises and update status and save exercises`() =
        runTest(testDispatcher) {
            // Given
            val scribbleId = 1L
            val exercises = listOf(
                com.scribblefit.core.model.Exercise(
                    id = 0,
                    canonicalName = "Push up",
                    muscleGroup = "Chest",
                    sets = emptyList()
                )
            )

            val existingScribble = Scribble(
                id = scribbleId,
                rawText = "text",
                parsedJson = null,
                status = ScribbleStatus.PENDING,
                createdAt = 123456789L,
                exercises = emptyList()
            )

            every { scribbleRepository.getScribble(scribbleId) } returns flowOf(existingScribble)
            coEvery { scribbleRepository.clearScribbleExercises(scribbleId) } returns Unit
            coEvery { scribbleRepository.saveScribbleExercises(scribbleId, exercises) } returns Unit
            coEvery { scribbleRepository.updateScribble(any()) } returns Unit

            // When
            val result = useCase(scribbleId, exercises)

            // Then
            assertTrue(result.isSuccess)

            coVerify {
                scribbleRepository.clearScribbleExercises(scribbleId)
                scribbleRepository.saveScribbleExercises(scribbleId, exercises)
                scribbleRepository.updateScribble(match {
                    it.id == scribbleId && it.status == ScribbleStatus.SUCCESS && it.parsedJson == null
                })
            }
        }
}