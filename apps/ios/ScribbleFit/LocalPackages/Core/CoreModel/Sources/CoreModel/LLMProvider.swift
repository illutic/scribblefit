import Foundation

public struct ParsedWorkoutResult: Sendable, Codable {
    public let exercises: [Exercise]
    public let rawText: String

    public init(exercises: [Exercise], rawText: String) {
        self.exercises = exercises
        self.rawText = rawText
    }
}

@MainActor
public protocol LLMService: Sendable {
    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult
    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight]
    func generateExerciseInsight(history: String) async throws -> AIInsight
    func isSupported() async -> Bool
}
