package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.ai.model.TelemetryData
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TelemetryRepositoryImplTest {

    private lateinit var api: ScribbleFitApi
    private lateinit var repository: TelemetryRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        repository = TelemetryRepositoryImpl(api)
    }

    @Test
    fun `reportError calls api with correct request`() = runTest {
        // Given
        val data = TelemetryData(
            rawText = "raw text",
            promptVersion = "1.0.0",
            errorMessage = "error",
            errorCode = "500",
            deviceModel = "Pixel 6"
        )
        coEvery { api.reportError(any()) } returns HttpStatusCode.Accepted

        // When
        val result = repository.reportError(data)

        // Then
        assertTrue(result.isSuccess)
        coVerify { 
            api.reportError(match { 
                it.rawText == data.rawText &&
                it.promptVersion == data.promptVersion &&
                it.errorMessage == data.errorMessage &&
                it.errorCode == data.errorCode &&
                it.deviceModel == data.deviceModel
            }) 
        }
    }

    @Test
    fun `reportError returns failure when api fails`() = runTest {
        // Given
        coEvery { api.reportError(any()) } throws Exception("Network error")

        // When
        val result = repository.reportError(mockk(relaxed = true))

        // Then
        assertTrue(result.isFailure)
    }
}
