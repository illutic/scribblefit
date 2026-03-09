import Foundation

struct GeminiRequest: Codable {
    let contents: [GeminiContent]
    let generationConfig: GeminiGenerationConfig
}

struct GeminiContent: Codable {
    let parts: [GeminiPart]
}

struct GeminiPart: Codable {
    let text: String
}

struct GeminiGenerationConfig: Codable {
    let responseMimeType: String
}

struct GeminiResponse: Codable {
    let candidates: [GeminiCandidate]
}

struct GeminiCandidate: Codable {
    let content: GeminiContent
}

// MARK: - OpenAI DTOs

struct OpenAIChatRequest: Codable {
    let model: String
    let messages: [OpenAIChatMessage]
    let responseFormat: OpenAIResponseFormat

    enum CodingKeys: String, CodingKey {
        case model, messages
        case responseFormat = "response_format"
    }
}

struct OpenAIChatMessage: Codable {
    let role: String
    let content: String
}

struct OpenAIResponseFormat: Codable {
    let type: String
}

struct OpenAIChatResponse: Codable {
    let choices: [OpenAIChatChoice]
}

struct OpenAIChatChoice: Codable {
    let message: OpenAIChatMessage
}

// MARK: - Analysis response DTOs

struct SuggestionResponse: Codable {
    let text: String
    let emoji: String
    let type: SuggestionType
}

struct SummaryResponse: Codable {
    let summaryText: String
    let highlights: [String]
    let muscleDistribution: [MuscleGroupStat]
    let focusArea: String
    let volumeDelta: Double
}

struct InsightResponse: Codable {
    let estimated1RM: Double
    let prDetected: Bool
    let trendDirection: InsightTrend
    let breakdownText: String
}
