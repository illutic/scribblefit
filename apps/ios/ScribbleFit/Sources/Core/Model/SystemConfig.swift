import Foundation

public struct SystemConfig: Equatable, Sendable, Codable {
    public var summaryPrompt: String
    public var suggestionPrompt: String
    public var insightPrompt: String
    public var parsePrompt: String
    public var preferredLlmProvider: LLMProvider
    public var updatedAt: Date
    public var preferredModel: String?
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
        preferredModel: String? = nil,
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
        self.preferredModel = preferredModel
        self.weightUnit = weightUnit
        self.themePreference = themePreference
        self.isDynamicTheme = isDynamicTheme
    }

    public static let defaultSuggestionPrompt = """
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

    public static let defaultSummaryPrompt = """
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

    public static let defaultInsightPrompt = """
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

    public static let defaultParsePrompt = """
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

public enum LLMProvider: String, Codable, Sendable {
    case gemini
    case local
    
    public var requiresApiKey: Bool {
        self == .gemini
    }
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
