package com.scribblefit.api.models

import kotlinx.serialization.Serializable

@Serializable
data class MetadataResponse(
    val status: String,
    val version: String
)
