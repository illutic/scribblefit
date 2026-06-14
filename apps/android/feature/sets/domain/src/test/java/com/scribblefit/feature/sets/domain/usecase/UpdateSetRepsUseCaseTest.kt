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

class UpdateSetRepsUseCaseTest {

    private val repository: SetRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: UpdateSetRepsUseCase

    @Before
    fun setup() {
        useCase = UpdateSetRepsUseCase(
            repository = repository,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success when repository update succeeds`() = runTest(testDispatcher) {
        val result = useCase(1L, 10)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke calls repository with correct setId and reps`() = runTest(testDispatcher) {
        useCase(42L, 15)

        coVerify(exactly = 1) { repository.updateSetReps(42L, 15) }
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest(testDispatcher) {
        coEvery { repository.updateSetReps(any(), any()) } throws RuntimeException("DB error")

        val result = useCase(1L, 10)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke works with zero reps`() = runTest(testDispatcher) {
        val result = useCase(1L, 0)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.updateSetReps(1L, 0) }
    }

    @Test
    fun `invoke works with large reps value`() = runTest(testDispatcher) {
        val result = useCase(1L, Int.MAX_VALUE)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.updateSetReps(1L, Int.MAX_VALUE) }
    }
}
