package com.scribblefit.api.services

import com.scribblefit.api.models.ConfigResponse

class ConfigService {
    fun getPromptConfig(): ConfigResponse {
        return ConfigResponse(
            version = "1.0.0",
            prompt = """
                You are a workout log parser. Your task is to take raw text from a user's workout log and convert it into a structured JSON format.
                Follow the schema strictly.
            """.trimIndent()
        )
    }
}
