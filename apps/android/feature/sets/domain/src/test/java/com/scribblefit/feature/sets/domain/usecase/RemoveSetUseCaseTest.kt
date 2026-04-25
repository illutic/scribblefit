package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.feature.sets.domain.SetRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RemoveSetUseCaseTest {

    private val setRepository = mockk<SetRepository>()
    private lateinit var removeSetUseCase: RemoveSetUseCase

    @Before
    fun setup() {
        removeSetUseCase = RemoveSetUseCase(setRepository)
    }

    @Test
    fun `invoke calls deleteSet on repository`() = runTest {
        val setId = 1L
        coEvery { setRepository.deleteSet(setId) } returns Unit

        removeSetUseCase(setId)

        coVerify { setRepository.deleteSet(setId) }
    }
}
