import Foundation

/**
 * Standardized result for workout parsing with rich metadata.
 */
public struct ParsedWorkoutResult: Sendable {
    public let workout: ParsedWorkout?
    public let rawText: String
    public let status: ParsingStatus
    public let modelUsed: String?
    public let processingTimeMs: Int64
    public let reasoning: String?
    public let error: String?

    public init(
        workout: ParsedWorkout?,
        rawText: String,
        status: ParsingStatus,
        modelUsed: String? = nil,
        processingTimeMs: Int64 = 0,
        reasoning: String? = nil,
        error: String? = nil
    ) {
        self.workout = workout
        self.rawText = rawText
        self.status = status
        self.modelUsed = modelUsed
        self.processingTimeMs = processingTimeMs
        self.reasoning = reasoning
        self.error = error
    }
}

public enum ParsingStatus: String, Codable, Sendable {
    case success = "SUCCESS"
    case partialSuccess = "PARTIAL_SUCCESS"
    case failure = "FAILURE"
}
