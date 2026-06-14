package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.feature.sets.domain.SetRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateSetWeightUseCaseTest {

    private val repository: SetRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: UpdateSetWeightUseCase

    @Before
    fun setup() {
        useCase = UpdateSetWeightUseCase(
            repository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success when repository update succeeds with weight`() = runTest(testDispatcher) {
        val result = useCase(1L, 100f)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke returns success when called with null weight (bodyweight)`() = runTest(testDispatcher) {
        val result = useCase(1L, null)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke calls repository with correct setId and weight`() = runTest(testDispatcher) {
        useCase(42L, 80.5f)

        coVerify(exactly = 1) { repository.updateSetWeight(42L, 80.5f) }
    }

    @Test
    fun `invoke calls repository with null weight`() = runTest(testDispatcher) {
        useCase(5L, null)

        coVerify(exactly = 1) { repository.updateSetWeight(5L, null) }
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.updateSetWeight(any(), any()) } throws RuntimeException("DB error")

        val result = useCase(1L, 100f)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke works with zero weight`() = runTest(testDispatcher) {
        val result = useCase(1L, 0f)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.updateSetWeight(1L, 0f) }
    }
}
