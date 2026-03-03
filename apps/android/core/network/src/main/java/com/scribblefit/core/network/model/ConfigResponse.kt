package com.scribblefit.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfigResponse(
    val version: String,
    val prompt: String
)
