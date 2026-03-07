package com.scribblefit.feature.ai.data.engine

object AnalysisPrompts {
    fun getSuggestionPrompt(context: String) = """
        You are ScribbleFit AI. Based on the following workout context, suggest today's workout.
        Context: $context
        
        Output valid JSON:
        {
          "text": "Short suggestion string",
          "emoji": "Relevant emoji",
          "type": "RECOVERY|PATTERN|MILESTONE|REST"
        }
    """.trimIndent()

    fun getSummaryPrompt(period: String, data: String) = """
        You are ScribbleFit AI. Analyze the following workout data for the period: $period.
        Data: $data
        
        Output valid JSON:
        {
          "summary_text": "2-sentence high-level summary",
          "highlights": ["Win 1", "Win 2"],
          "focus_muscle_groups": ["Group 1", "Group 2"],
          "volume_delta": 0.12
        }
    """.trimIndent()

    fun getExerciseInsightPrompt(name: String, history: String) = """
        You are ScribbleFit AI. Analyze the history for the exercise: $name.
        History: $history
        
        Output valid JSON:
        {
          "estimated_1rm": 225.0,
          "pr_detected": true,
          "trend_direction": "IMPROVING|STABLE|PLATEAUED|DECLINING",
          "breakdown_text": "Detailed analysis of progress"
        }
    """.trimIndent()
}
