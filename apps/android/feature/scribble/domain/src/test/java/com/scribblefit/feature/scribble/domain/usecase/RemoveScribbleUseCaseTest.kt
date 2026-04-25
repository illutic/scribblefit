package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoveScribbleUseCaseTest {

    private val scribbleRepository = mockk<ScribbleRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = RemoveScribbleUseCase(
        scribbleRepository,
        testDispatcher
    )

    @Test
    fun `when invoked, should delete the scribble`() =
        runTest(testDispatcher) {
            // Given
            val scribbleId = 1L
            coEvery { scribbleRepository.deleteScribble(scribbleId) } returns Unit

            // When
            val result = useCase(scribbleId)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) {
                scribbleRepository.deleteScribble(scribbleId)
            }
        }

    @Test
    fun `when repository fails, should return failure`() = runTest(testDispatcher) {
        // Given
        val scribbleId = 1L
        coEvery { scribbleRepository.deleteScribble(scribbleId) } throws RuntimeException("DB Error")

        // When
        val result = useCase(scribbleId)

        // Then
        assertTrue(result.isFailure)
    }
}
