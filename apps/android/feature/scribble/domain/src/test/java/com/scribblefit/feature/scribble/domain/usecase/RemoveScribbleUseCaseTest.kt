package com.scribblefit.feature.scribble.domain.usecase

import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoveScribbleUseCaseTest {

    private val repository: ScribbleRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: RemoveScribbleUseCase

    @Before
    fun setup() {
        useCase = RemoveScribbleUseCase(
            scribbleRepository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success when repository deletes successfully`() = runTest(testDispatcher) {
        val result = useCase(1L)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke calls repository deleteScribble with correct id`() = runTest(testDispatcher) {
        useCase(42L)

        coVerify(exactly = 1) { repository.deleteScribble(42L) }
    }

    @Test
    fun `invoke returns failure when repository throws exception`() = runTest(testDispatcher) {
        val repo = mockk<ScribbleRepository>()
        io.mockk.coEvery { repo.deleteScribble(any()) } throws RuntimeException("DB error")
        val useCase = RemoveScribbleUseCase(
            scribbleRepository = repo,
            coroutineDispatcher = testDispatcher
        )

        val result = useCase(1L)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `invoke works with id zero`() = runTest(testDispatcher) {
        val result = useCase(0L)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.deleteScribble(0L) }
    }

    @Test
    fun `invoke works with large ids`() = runTest(testDispatcher) {
        val result = useCase(Long.MAX_VALUE)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.deleteScribble(Long.MAX_VALUE) }
    }
}
