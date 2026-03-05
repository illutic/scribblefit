package com.scribblefit.feature.ai.domain.repository

interface AuthRepository {
    suspend fun login(deviceId: String): Result<Unit>
    suspend fun isLogged(): Boolean
}
