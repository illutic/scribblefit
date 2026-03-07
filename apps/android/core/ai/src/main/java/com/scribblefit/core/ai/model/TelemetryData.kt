package com.scribblefit.core.ai.model

data class TelemetryData(
    val rawText: String,
    val promptVersion: String,
    val errorMessage: String,
    val errorCode: String? = null,
    val deviceModel: String? = null
)
