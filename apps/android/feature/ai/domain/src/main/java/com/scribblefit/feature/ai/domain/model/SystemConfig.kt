package com.scribblefit.feature.ai.domain.model

data class SystemConfig(
    val promptVersion: String,
    val promptText: String,
    val exerciseVersion: String = "0.0.0",
    val preferredLlmProvider: LLMProvider = LLMProvider.PROXY,
    val preferredModel: String = "",
    val parsingMode: String = "managed",
    val weightUnit: String = "lbs",
    val themePreference: String = "system",
    val updatedAt: Long
) {
    companion object {
        val defaultPrompt: String = """
            You are ScribbleFit AI, a fitness parsing assistant.
            Parse raw gym shorthand into this JSON schema:
            {"date":"YYYY-MM-DD","location":"String or null","exercises":[{"canonical_name":"String","muscle_group":"String","sets":[{"weight":number,"reps":integer,"rpe":number|null,"notes":"String|null"}]}]}
            Output ONLY valid JSON. No markdown, no extra text.
        """.trimIndent()
    }
}

enum class LLMProvider(val rawValue: String) {
    OPENAI("openai"), GEMINI("gemini"), LOCAL("local"), PROXY("proxy")
}
