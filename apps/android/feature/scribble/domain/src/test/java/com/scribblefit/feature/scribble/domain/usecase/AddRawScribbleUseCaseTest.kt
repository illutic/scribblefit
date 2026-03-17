package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.EmptyScribbleTextException
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class AddRawScribbleUseCaseTest {

    private val scribbleRepository = mockk<ScribbleRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = AddRawScribbleUseCase(scribbleRepository, testDispatcher)

    @Test
    fun `when text is not blank, should insert scribble and return success`() = runTest(testDispatcher) {
        // Given
        val text = "Bench 135x5"
        coEvery { scribbleRepository.insertScribble(any()) } returns 1L

        // When
        val result = useCase(text)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            scribbleRepository.insertScribble(match {
                it.rawText == text && it.status == ScribbleStatus.PENDING
            })
        }
    }

    @Test
    fun `when text is blank, should return failure with EmptyScribbleTextException`() = runTest(testDispatcher) {
        // Given
        val text = "   "

        // When
        val result = useCase(text)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is EmptyScribbleTextException)
        coVerify(exactly = 0) { scribbleRepository.insertScribble(any()) }
    }
}
