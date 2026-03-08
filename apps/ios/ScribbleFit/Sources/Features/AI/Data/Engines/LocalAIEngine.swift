import Foundation

public final class LocalAIEngine: LLMEngine {
    public init() {}

    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        ParsedWorkoutResult(
            workout: nil,
            rawText: rawText,
            status: .failure,
            error: "Local AI not yet available on this device"
        )
    }
}
