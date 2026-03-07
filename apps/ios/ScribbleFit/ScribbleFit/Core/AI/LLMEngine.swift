import Foundation

public enum LLMProvider: String, Codable, Sendable {
    case proxy = "proxy"
    case openai = "openai"
    case gemini = "gemini"
    case local = "local"
}

public protocol LLMEngine: Sendable {
    func parseWorkout(rawText: String) async -> ParsedWorkoutResult
}
