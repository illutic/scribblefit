package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.AuthRequest
import com.scribblefit.feature.ai.domain.repository.AuthRepository
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ScribbleFitApi,
    private val secureKeyStorage: SecureKeyStorage
) : AuthRepository {

    override suspend fun login(deviceId: String): Result<Unit> = runCatching {
        val response = api.login(AuthRequest(deviceId))
        secureKeyStorage.saveAuthToken(response.token)
    }

    override suspend fun isLogged(): Boolean {
        return secureKeyStorage.getAuthToken() != null
    }
}
