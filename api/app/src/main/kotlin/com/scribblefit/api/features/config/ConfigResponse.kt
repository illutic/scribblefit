package com.scribblefit.api.features.config

import kotlinx.serialization.Serializable

@Serializable
data class ConfigResponse(
    val version: String,
    val prompt: String
)
