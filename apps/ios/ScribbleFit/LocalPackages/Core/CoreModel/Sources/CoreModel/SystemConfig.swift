import Foundation

public struct SystemConfig: Equatable, Sendable, Codable {
    public var summaryPrompt: String
    public var suggestionPrompt: String
    public var insightPrompt: String
    public var parsePrompt: String
    public var preferredLlmProvider: LLMProvider
    public var updatedAt: Date
    public var weightUnit: WeightUnit
    public var themePreference: ThemePreference
    public var isDynamicTheme: Bool

    public init(
        summaryPrompt: String = Self.defaultSummaryPrompt,
        suggestionPrompt: String = Self.defaultSuggestionPrompt,
        insightPrompt: String = Self.defaultInsightPrompt,
        parsePrompt: String = Self.defaultParsePrompt,
        preferredLlmProvider: LLMProvider = .local,
        updatedAt: Date = Date(),
        weightUnit: WeightUnit = .kgs,
        themePreference: ThemePreference = .system,
        isDynamicTheme: Bool = false
    ) {
        self.summaryPrompt = summaryPrompt
        self.suggestionPrompt = suggestionPrompt
        self.insightPrompt = insightPrompt
        self.parsePrompt = parsePrompt
        self.preferredLlmProvider = preferredLlmProvider
        self.updatedAt = updatedAt
        self.weightUnit = weightUnit
        self.themePreference = themePreference
        self.isDynamicTheme = isDynamicTheme
    }

    public static let defaultSuggestionPrompt = """
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
        Generate one actionable training suggestion based on the workout context below.
        Output ONLY this JSON (no markdown, no extra text):
        {
          "text": "suggestion text", 
          "emoji": "emoji",
          "type":"RECOVERY|PATTERN|MILESTONE|REST"
        }
        type must be exactly one of: RECOVERY, PATTERN, MILESTONE, REST
        {{/system}}

        Context:
        {{context}}
        """

    public static let defaultSummaryPrompt = """
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
        {{/system}}

        Data:
        {{workoutData}}
        """

    public static let defaultInsightPrompt = """
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
        {{/system}}

        History:
        {{exerciseHistory}}
        """

    public static let defaultParsePrompt = """
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
        Parse raw gym shorthand into this JSON schema:
        {
          "exercises": [{ 
            "canonical_name": "String", 
            "muscle_group": "String", 
            "sets": [{ "weight": number|null, "reps": integer, "setNumber": integer, "rpe": number|null, "notes": "String|null" }],
            "estimated_1rm": number|null,
            "intensity": number|null
          }]
        }
        Use Epley formula (weight * (1 + reps/30)) for 1RM estimate if possible. 
        Intensity should be a decimal (e.g. 0.85 for 85%).
        Reps are always required, weight can be null if not provided or not parsable.
        Output ONLY valid JSON. No extra text, no markdown, no apologies. If you can't parse any exercises, return {"exercises": []}.
        {{/system}}

        Input: {{rawText}}
    """
}

public enum LLMProvider: String, Codable, Sendable {
    case gemini
    case local
}
public enum WeightUnit: String, Codable, Sendable {
    case lbs
    case kgs
}

public enum ThemePreference: String, Codable, Sendable {
    case light
    case dark
    case system
}
