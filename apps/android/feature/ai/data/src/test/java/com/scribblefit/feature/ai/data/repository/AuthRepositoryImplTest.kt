package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.AuthResponse
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private lateinit var api: ScribbleFitApi
    private lateinit var secureKeyStorage: SecureKeyStorage
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        secureKeyStorage = mockk(relaxed = true)
        repository = AuthRepositoryImpl(api, secureKeyStorage)
    }

    @Test
    fun `login success saves token`() = runTest {
        // Given
        val deviceId = "device123"
        val token = "jwt.token.here"
        coEvery { api.login(any()) } returns AuthResponse(token, 123456789L)

        // When
        val result = repository.login(deviceId)

        // Then
        assertTrue(result.isSuccess)
        coVerify { api.login(match { it.deviceId == deviceId }) }
        coVerify { secureKeyStorage.saveAuthToken(token) }
    }

    @Test
    fun `login failure returns failure`() = runTest {
        // Given
        coEvery { api.login(any()) } throws Exception("Network error")

        // When
        val result = repository.login("device123")

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { secureKeyStorage.saveAuthToken(any()) }
    }

    @Test
    fun `isLogged returns true when token exists`() = runTest {
        // Given
        coEvery { secureKeyStorage.getAuthToken() } returns "some.token"

        // When
        val result = repository.isLogged()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isLogged returns false when token is null`() = runTest {
        // Given
        coEvery { secureKeyStorage.getAuthToken() } returns null

        // When
        val result = repository.isLogged()

        // Then
        assertFalse(result)
    }
}
