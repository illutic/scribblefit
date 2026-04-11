package com.scribblefit.core.config.domain

data class SystemConfig(
    val summaryPrompt: String,
    val suggestionPrompt: String,
    val insightPrompt: String,
    val parsePrompt: String,
    val preferredLlmProvider: LLMProvider,
    val updatedAt: Long,
    val preferredModel: String?,
    val weightUnit: Weight,
    val themePreference: ThemePreference,
    val isDynamicTheme: Boolean,
) {
    companion object {
        const val SUGGESTION_PROMPT = """
            You are ScribbleFit AI, a fitness analysis assistant.
            Generate one actionable training suggestion based on the workout context below.
            Output ONLY this JSON (no markdown, no extra text):
            {
              "text": "suggestion text", 
              "emoji": "emoji",
              "type":"RECOVERY|PATTERN|MILESTONE|REST"
            }
            type must be exactly one of: RECOVERY, PATTERN, MILESTONE, REST
            """

        const val SUMMARY_PROMPT = """
            You are ScribbleFit AI, a fitness analysis assistant.
            Analyze the workout data below and generate a list of structured insights.
            Output ONLY this JSON schema (no markdown, no extra text):
            [
              {
                "insightType": "summary",
                "text": "A brief overall summary of the workout session."
              },
              {
                "insightType": "trend",
                "text": "An observation about progress, consistency, or volume patterns."
              },
              {
                "insightType": "advice",
                "text": "One actionable tip for recovery, technique, or future progression."
              }
            ]
            The list should contain at least one of each type if possible.
            """

        const val INSIGHT_PROMPT = """
            You are ScribbleFit AI, a fitness analysis assistant.
            Analyze the exercise history below and generate a performance insight.
            Output ONLY this JSON (no markdown, no extra text):
            {
              "estimated1RM": number,
              "prDetected": true|false,
              "trendDirection":"IMPROVING|STABLE|PLATEAUED|DECLINING",
              "breakdownText":"2-3 sentence analysis"
            }
            Use Epley formula (weight * (1 + reps/30)) for 1RM estimate. 
            trendDirection must be exactly one of: IMPROVING, STABLE, PLATEAUED, DECLINING
            """

        const val PARSE_PROMPT = """
            You are ScribbleFit AI, a fitness parsing assistant.
            Parse raw gym shorthand into this JSON schema:
            {
              "exercises": [{ 
                "canonical_name": "String", 
                "muscle_group": "String", 
                "sets": [{ "weight": number, "reps": integer, "setNumber": integer, "rpe": number|null, "notes": "String|null" }],
                "estimated_1rm": number|null,
                "intensity": number|null,
                "improvement": number|null
              }]
            }
            Use Epley formula (weight * (1 + reps/30)) for 1RM estimate if possible. 
            Intensity should be a decimal (e.g. 0.85 for 85%).
            Improvement is the weight change vs a previous session (if detectable from context, otherwise null).
            Output ONLY valid JSON. No markdown, no extra text.
        """
    }
}

enum class LLMProvider(
    val requiresApiKey: Boolean
) {
    GEMINI(requiresApiKey = true), LOCAL(requiresApiKey = false)
}

enum class Weight {
    LBS, KGS
}

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}
