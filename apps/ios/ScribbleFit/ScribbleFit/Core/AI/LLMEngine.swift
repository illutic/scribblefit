import Foundation

public protocol LLMEngine: Sendable {
    func parseWorkout(rawText: String) async throws -> ParsedWorkout
}
