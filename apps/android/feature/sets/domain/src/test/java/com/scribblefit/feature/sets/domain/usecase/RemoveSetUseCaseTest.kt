package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.feature.sets.domain.SetRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RemoveSetUseCaseTest {

    private val repository: SetRepository = mockk(relaxed = true)
    private lateinit var useCase: RemoveSetUseCase

    @Before
    fun setup() {
        useCase = RemoveSetUseCase(setRepository = repository)
    }

    @Test
    fun `invoke calls deleteSet on repository with correct id`() = runTest {
        useCase(42L)

        coVerify(exactly = 1) { repository.deleteSet(42L) }
    }

    @Test
    fun `invoke calls deleteSet with id zero`() = runTest {
        useCase(0L)

        coVerify(exactly = 1) { repository.deleteSet(0L) }
    }

    @Test
    fun `invoke calls deleteSet with large id`() = runTest {
        useCase(Long.MAX_VALUE)

        coVerify(exactly = 1) { repository.deleteSet(Long.MAX_VALUE) }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke propagates repository exception`() = runTest {
        coEvery { repository.deleteSet(any()) } throws RuntimeException("DB error")

        useCase(1L)
    }
}
