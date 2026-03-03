import Foundation

public protocol LLMEngine: Sendable {
    func parseWorkout(rawText: String, prompt: String) async throws -> ParsedWorkout
}
