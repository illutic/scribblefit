package com.scribblefit.feature.settings.domain

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ClearUserDataUseCaseTest {

    private val repository: SettingsRepository = mockk(relaxed = true)
    private lateinit var useCase: ClearUserDataUseCase

    @Before
    fun setup() {
        useCase = ClearUserDataUseCase(repository = repository)
    }

    @Test
    fun `invoke calls clearAllUserData on repository`() = runTest {
        useCase()

        coVerify(exactly = 1) { repository.clearAllUserData() }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke propagates exception from repository`() = runTest {
        io.mockk.coEvery { repository.clearAllUserData() } throws RuntimeException("DB error")

        useCase()
    }

    @Test
    fun `invoke calls repository exactly once`() = runTest {
        useCase()
        useCase()

        coVerify(exactly = 2) { repository.clearAllUserData() }
    }
}
