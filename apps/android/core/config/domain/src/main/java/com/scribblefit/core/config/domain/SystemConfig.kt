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
            Analyze the workout data below and generate a training summary.
            Output ONLY this JSON (no markdown, no extra text):
            {
              "summaryText": "2-3 sentence summary",
              "highlights": ["highlight 1", "highlight 2"],
              "muscleDistribution": [
                {
                  "muscleGroup": "name", 
                  "volumePercentage":number
                }
              ],
              "focusArea": "primary muscle group",
              "volumeDelta":number
            }
            muscleDistribution percentages must sum to 100. 
            volumeDelta is percentage change vs previous period.
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
              "date": "YYYY-MM-DD",
              "exercises": [{ "canonical_name": "String", "muscle_group": "String", "sets": [{ "weight": number, "reps": integer, "rpe": number|null, "notes": "String|null" }] }]
            }
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
