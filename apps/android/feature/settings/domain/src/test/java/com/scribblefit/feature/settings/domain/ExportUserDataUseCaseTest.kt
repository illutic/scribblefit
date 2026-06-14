package com.scribblefit.feature.settings.domain

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExportUserDataUseCaseTest {

    private val repository: SettingsRepository = mockk()
    private lateinit var useCase: ExportUserDataUseCase

    @Before
    fun setup() {
        useCase = ExportUserDataUseCase(repository = repository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val expectedData = "exported data content"
        coEvery { repository.exportUserData() } returns flowOf(expectedData)

        val result = useCase().first()

        assertEquals(expectedData, result)
    }

    @Test
    fun `invoke returns multiple chunks from repository`() = runTest {
        val chunks = listOf("chunk1", "chunk2", "chunk3")
        coEvery { repository.exportUserData() } returns flowOf(*chunks.toTypedArray())

        val result = mutableListOf<String>()
        useCase().collect { result.add(it) }

        assertEquals(chunks, result)
    }

    @Test(expected = RuntimeException::class)
    fun `invoke propagates exception from repository`() = runTest {
        coEvery { repository.exportUserData() } throws RuntimeException("Export error")

        useCase()
    }
}
