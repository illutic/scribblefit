package com.scribblefit.core.ai.engine

interface AuthRepository {
    suspend fun login(deviceId: String): Result<Unit>
    suspend fun isLogged(): Boolean
}
