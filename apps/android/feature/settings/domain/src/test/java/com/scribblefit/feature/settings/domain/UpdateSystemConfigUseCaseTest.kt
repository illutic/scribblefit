package com.scribblefit.feature.settings.domain

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SystemConfig
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateSystemConfigUseCaseTest {

    private val configRepository: ConfigRepository = mockk(relaxed = true)
    private lateinit var useCase: UpdateSystemConfigUseCase

    @Before
    fun setup() {
        useCase = UpdateSystemConfigUseCase(configRepository = configRepository)
    }

    @Test
    fun `invoke calls updateConfig on repository with provided config`() = runTest {
        val config = SystemConfig()

        useCase(config)

        coVerify(exactly = 1) { configRepository.updateConfig(config) }
    }

    @Test
    fun `invoke passes the exact config to repository`() = runTest {
        val config = SystemConfig()

        useCase(config)

        coVerify { configRepository.updateConfig(config) }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke propagates exception from repository`() = runTest {
        io.mockk.coEvery { configRepository.updateConfig(any()) } throws RuntimeException("Config error")
        val config = SystemConfig()

        useCase(config)
    }

    @Test
    fun `invoke can be called multiple times`() = runTest {
        val config1 = SystemConfig()
        val config2 = SystemConfig()

        useCase(config1)
        useCase(config2)

        coVerify(exactly = 2) { configRepository.updateConfig(any()) }
    }
}
