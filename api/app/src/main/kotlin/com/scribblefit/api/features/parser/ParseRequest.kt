package com.scribblefit.api.features.parser

import kotlinx.serialization.Serializable

@Serializable
data class ParseRequest(
    val rawText: String,
    val prompt: String? = null
)
