import Foundation

public enum AnalysisPrompts {
    public static func getSuggestionPrompt(context: String) -> String {
        """
        You are ScribbleFit AI. Based on the following workout context, suggest today's workout.
        Context: \(context)
        
        Output valid JSON:
        {
          "text": "Short suggestion string",
          "emoji": "Relevant emoji",
          "type": "recovery|pattern|milestone|rest"
        }
        """
    }

    public static func getSummaryPrompt(period: String, data: String) -> String {
        """
        You are ScribbleFit AI. Analyze the following workout data for the period: \(period).
        Data: \(data)
        
        Output valid JSON:
        {
          "summary_text": "2-sentence high-level summary",
          "highlights": ["Win 1", "Win 2"],
          "focus_muscle_groups": ["Group 1", "Group 2"],
          "volume_delta": 0.12
        }
        """
    }

    public static func getExerciseInsightPrompt(name: String, history: String) -> String {
        """
        You are ScribbleFit AI. Analyze the history for the exercise: \(name).
        History: \(history)
        
        Output valid JSON:
        {
          "estimated_1rm": 225.0,
          "pr_detected": true,
          "trend_direction": "improving|stable|plateaued|declining",
          "breakdown_text": "Detailed analysis of progress"
        }
        """
    }
}
