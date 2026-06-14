package com.scribblefit.feature.settings.domain

import com.scribblefit.feature.ai.domain.LLMEngine
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CheckLocalSupportUseCaseTest {

    private val localEngine: LLMEngine = mockk()
    private lateinit var useCase: CheckLocalSupportUseCase

    @Before
    fun setup() {
        useCase = CheckLocalSupportUseCase(localEngine = localEngine)
    }

    @Test
    fun `invoke returns true when local engine is supported`() = runTest {
        coEvery { localEngine.isSupported() } returns true

        val result = useCase()

        assertTrue(result)
    }

    @Test
    fun `invoke returns false when local engine is not supported`() = runTest {
        coEvery { localEngine.isSupported() } returns false

        val result = useCase()

        assertFalse(result)
    }

    @Test(expected = RuntimeException::class)
    fun `invoke propagates exception from engine`() = runTest {
        coEvery { localEngine.isSupported() } throws RuntimeException("Engine error")

        useCase()
    }
}
