package com.scribblefit.api.features.config

import com.scribblefit.api.features.config.ConfigResponse
import com.scribblefit.api.features.config.MetadataResponse

interface ConfigService {
    fun getPromptConfig(): ConfigResponse
    fun getMetadata(): MetadataResponse
}
