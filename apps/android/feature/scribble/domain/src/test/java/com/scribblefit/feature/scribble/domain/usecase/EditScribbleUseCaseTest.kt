package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.EmptyScribbleTextException
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

class EditScribbleUseCaseTest {

    private val scribbleRepository = mockk<ScribbleRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = EditScribbleUseCase(scribbleRepository, testDispatcher)

    @Test
    fun `when scribble exists and text is not blank, should update scribble and return success`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        val oldText = "Old text"
        val newText = "New text"
        val existingScribble = Scribble(
            id = scribbleId,
            rawText = oldText,
            parsedJson = null,
            status = ScribbleStatus.PENDING,
            createdAt = 123456789L,
            exercises = emptyList()
        )
        
        every { scribbleRepository.getScribble(scribbleId) } returns flowOf(existingScribble)
        coEvery { scribbleRepository.updateScribble(any()) } returns Unit

        // When
        val result = useCase(scribbleId, newText)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            scribbleRepository.updateScribble(match {
                it.id == scribbleId && it.rawText == newText && it.status == ScribbleStatus.PENDING
            })
        }
    }

    @Test
    fun `when scribble does not exist, should return failure with ScribbleNotFoundException`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        every { scribbleRepository.getScribble(scribbleId) } returns flowOf()

        // When
        val result = useCase(scribbleId, "Some text")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ScribbleNotFoundException)
        coVerify(exactly = 0) { scribbleRepository.updateScribble(any()) }
    }

    @Test
    fun `when text is blank, should return failure with EmptyScribbleTextException`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        val existingScribble = Scribble(
            id = scribbleId,
            rawText = "Old text",
            parsedJson = null,
            status = ScribbleStatus.PENDING,
            createdAt = 123456789L,
            exercises = emptyList()
        )
        every { scribbleRepository.getScribble(scribbleId) } returns flowOf(existingScribble)

        // When
        val result = useCase(scribbleId, "   ")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is EmptyScribbleTextException)
        coVerify(exactly = 0) { scribbleRepository.updateScribble(any()) }
    }
}
