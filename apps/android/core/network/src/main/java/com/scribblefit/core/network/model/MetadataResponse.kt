package com.scribblefit.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class MetadataResponse(
    val status: String,
    val version: String
)
