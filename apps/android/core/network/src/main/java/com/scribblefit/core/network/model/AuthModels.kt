package com.scribblefit.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val deviceId: String,
    val subscriptionId: String? = null
)

@Serializable
data class AuthResponse(
    val token: String,
    val expiresAt: Long
)
