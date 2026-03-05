package com.scribblefit.api.features.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

interface AuthService {
    fun authenticate(request: AuthRequest): AuthResponse
}

class AuthServiceImpl : AuthService {
    private val jwtSecret = System.getenv("JWT_SECRET") ?: "scribblefit-secret-2024"
    private val jwtIssuer = "scribblefit.api"
    private val jwtAudience = "scribblefit.app"
    private val expirationMs = 30L * 24 * 60 * 60 * 1000 // 30 days

    override fun authenticate(request: AuthRequest): AuthResponse {
        val expiresAt = Date(System.currentTimeMillis() + expirationMs)
        
        val token = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("deviceId", request.deviceId)
            .withClaim("subscriptionId", request.subscriptionId ?: "free")
            .withExpiresAt(expiresAt)
            .sign(Algorithm.HMAC256(jwtSecret))
            
        return AuthResponse(token, expiresAt.time)
    }
}
