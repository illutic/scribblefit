package com.scribblefit.feature.ai.domain.engine

interface AuthRepository {
    suspend fun login(deviceId: String): Result<Unit>
    suspend fun isLogged(): Boolean
}
