package com.scribblefit.api.services

import com.scribblefit.api.models.ConfigResponse
import com.scribblefit.api.models.MetadataResponse

interface ConfigService {
    fun getPromptConfig(): ConfigResponse
    fun getMetadata(): MetadataResponse
}
