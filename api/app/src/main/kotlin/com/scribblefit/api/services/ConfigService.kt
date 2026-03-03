package com.scribblefit.api.services

import com.scribblefit.api.models.ConfigResponse

interface ConfigService {
    fun getPromptConfig(): ConfigResponse
}
