package com.scribblefit.api.features.config

import kotlinx.serialization.Serializable

@Serializable
data class MetadataResponse(
    val status: String,
    val version: String
)
