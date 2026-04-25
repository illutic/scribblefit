package com.scribblefit.core.config.domain

data class SystemConfig(
    val localConfig: LocalConfig = LocalConfig(),
    val remoteConfig: RemoteConfig = RemoteConfig()
)

data class LocalConfig(
    val preferredLlmProvider: LLMProvider = LLMProvider.GEMINI,
    val weightUnit: Weight = Weight.KGS,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val isDynamicTheme: Boolean = true,
)

data class RemoteConfig(
    val suggestionPrompt: String = SUGGESTION_PROMPT,
    val summaryPrompt: String = SUMMARY_PROMPT,
    val insightPrompt: String = INSIGHT_PROMPT,
    val parsePrompt: String = PARSE_PROMPT,
) {
    companion object {
        const val SUGGESTION_PROMPT = """
            ---
            model: gemini-2.5-flash-lite
            config:
              responseMimeType: application/json
              temperature: 0.5
            input:
              schema:
                context: string
            ---
            {{#system}}
            You are ScribbleFit AI, a fitness analysis assistant.
            Generate one actionable training suggestion based on the workout context provided within <workout_context> tags.
            Output ONLY this JSON schema (no markdown, no extra text):
            [
              {
                "insightType": "advice",
                "text": "A suggestion for recovery, pattern, milestone or rest"
              }
            ]
            {{/system}}

            Context:
            {{context}}
            """

        const val SUMMARY_PROMPT = """
            ---
            model: gemini-2.5-flash-lite
            config:
              responseMimeType: application/json
              temperature: 0.4
            input:
              schema:
                workoutData: string
            ---
            {{#system}}
            You are ScribbleFit AI, a fitness analysis assistant.
            Analyze the workout data provided within <workout_history> tags and generate a list of structured insights.
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
            {{/system}}

            Data:
            {{workoutData}}
            """

        const val INSIGHT_PROMPT = """
            ---
            model: gemini-2.5-flash-lite
            config:
              responseMimeType: application/json
              temperature: 0.2
            input:
              schema:
                exerciseHistory: string
            ---
            {{#system}}
            You are ScribbleFit AI, a fitness analysis assistant.
            Analyze the exercise history provided within <exercise_history> tags and generate a performance insight.
            Output ONLY this JSON (no markdown, no extra text):
            {
              "insightType": "trend",
              "text": "2-3 sentence analysis of progress, estimated 1RM, and trends."
            }
            Use Epley formula (weight * (1 + reps/30)) for 1RM estimate in the text.
            {{/system}}

            History:
            {{exerciseHistory}}
            """

        const val PARSE_PROMPT = """
            ---
            model: gemini-2.5-flash-lite
            config:
              responseMimeType: application/json
              temperature: 0.1
            input:
              schema:
                rawText: string
            ---
            {{#system}}
            You are ScribbleFit AI, a fitness parsing assistant.
            Parse raw gym shorthand provided within <workout_scribble> tags into this JSON schema:
            {
              "exercises": [
                { 
                  "canonicalName": "String", 
                  "muscleGroup": "String", 
                  "sets": [{ "setNumber": integer, "reps": integer, "weight": number|null, "rpe": number|null, "notes": "String|null" }],
                  "estimated1RM": number|null,
                  "intensity": number|null
                }
              ]
            }
            Use Epley formula (weight * (1 + reps/30)) for 1RM estimate if possible. 
            Intensity should be a decimal (e.g. 0.85 for 85%).
            Reps are always required, weight can be null if not provided or not parsable.
            Output ONLY valid JSON. No extra text, no markdown, no apologies. If you can't parse any exercises, return {"exercises": []}.
            {{/system}}

            Input: {{rawText}}
        """
    }
}

enum class LLMProvider {
    GEMINI, LOCAL
}

enum class Weight {
    LBS, KGS
}

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}
