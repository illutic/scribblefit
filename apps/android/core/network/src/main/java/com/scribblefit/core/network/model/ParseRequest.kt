package com.scribblefit.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ParseRequest(
    val rawText: String,
    val prompt: String
)
