package com.scribblefit.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TelemetryRequest(
    val rawText: String,
    val promptVersion: String,
    val errorMessage: String,
    val errorCode: String? = null,
    val deviceModel: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
