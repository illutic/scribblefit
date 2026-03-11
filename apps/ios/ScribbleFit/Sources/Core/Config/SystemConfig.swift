import Foundation

public struct SystemConfigDomain: Sendable {
    public let summaryPrompt: String
    public let suggestionPrompt: String
    public let insightPrompt: String
    public let parsePrompt: String
    public let preferredLlmProvider: LLMProvider
    public let preferredModel: String?
    public let weightUnit: Weight
    public let themePreference: ThemePreference
    public let updatedAt: Date

    public init(
        summaryPrompt: String = SystemConfigDomain.SUMMARY_PROMPT,
        suggestionPrompt: String = SystemConfigDomain.SUGGESTION_PROMPT,
        insightPrompt: String = SystemConfigDomain.INSIGHT_PROMPT,
        parsePrompt: String = SystemConfigDomain.PARSE_PROMPT,
        preferredLlmProvider: LLMProvider = .proxy,
        preferredModel: String? = nil,
        weightUnit: Weight = .lbs,
        themePreference: ThemePreference = .system,
        updatedAt: Date = Date()
    ) {
        self.summaryPrompt = summaryPrompt
        self.suggestionPrompt = suggestionPrompt
        self.insightPrompt = insightPrompt
        self.parsePrompt = parsePrompt
        self.preferredLlmProvider = preferredLlmProvider
        self.preferredModel = preferredModel
        self.weightUnit = weightUnit
        self.themePreference = themePreference
        self.updatedAt = updatedAt
    }

    public static let SUGGESTION_PROMPT = """
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

    public static let SUMMARY_PROMPT = """
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

    public static let INSIGHT_PROMPT = """
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

    public static let PARSE_PROMPT = """
        You are ScribbleFit AI, a fitness parsing assistant.
        Parse raw gym shorthand into this JSON schema:
        {
          "date": "YYYY-MM-DD",
          "exercises": [{ "canonical_name": "String", "muscle_group": "String", "sets": [{ "weight": number, "reps": integer, "rpe": number|null, "notes": "String|null" }] }]
        }
        Output ONLY valid JSON. No markdown, no extra text.
        """
}

public enum LLMProvider: String, Codable, Sendable {
    case openai, gemini, local, proxy
}

public enum Weight: String, Codable, Sendable {
    case lbs, kgs
}

public enum ThemePreference: String, Codable, Sendable {
    case light, dark, system
}
