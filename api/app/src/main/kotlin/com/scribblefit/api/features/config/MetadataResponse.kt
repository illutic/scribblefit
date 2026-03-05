package com.scribblefit.api.features.config

import kotlinx.serialization.Serializable

@Serializable
data class MetadataResponse(
    val status: String,
    val appVersion: String,
    val promptVersion: String,
    val exerciseVersion: String
)
