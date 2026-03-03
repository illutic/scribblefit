package com.scribblefit.api.models

import kotlinx.serialization.Serializable

@Serializable
data class ConfigResponse(
    val version: String,
    val prompt: String
)
