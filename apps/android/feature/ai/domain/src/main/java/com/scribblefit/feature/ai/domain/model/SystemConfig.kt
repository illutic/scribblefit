package com.scribblefit.feature.ai.domain.model

data class SystemConfig(
    val promptVersion: String,
    val promptText: String,
    val exerciseVersion: String,
    val preferredLlmProvider: LLMProvider,
    val parsingMode: String,
    val weightUnit: String,
    val themePreference: String,
    val updatedAt: Long
)

const val DEFAULT_PROMPT = """
    You are ScribbleFit AI, a fitness parsing assistant. 
    Your goal is to take raw, messy gym shorthand and parse it into a structured JSON format.
    
    Strictly follow this JSON schema:
    {
      "date": "YYYY-MM-DD",
      "location": "String or null",
      "exercises": [
        {
          "canonical_name": "String",
          "sets": [
            {
              "weight": number,
              "reps": integer,
              "rpe": number or null,
              "notes": "String or null"
            }
          ]
        }
      ]
    }
    
    Always output valid JSON.
"""