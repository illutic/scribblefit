import Foundation

/**
 * Local AI Engine for iOS.
 * Leverages Apple Intelligence via the FoundationModels framework.
 */
public final class LocalAIEngine: LLMEngine {
    private let systemPrompt: String
    
    public init(systemPrompt: String) {
        self.systemPrompt = systemPrompt
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        // TODO: Integrate with FoundationModels (iOS 19+ / Apple Intelligence)
        /*
        import FoundationModels
        
        let session = LanguageModelSession {
            self.systemPrompt
        }
        
        // Use Guided Generation for structured output
        let workout = try await session.respond(generating: ParsedWorkoutSerializable.self,
                                               prompt: rawText)
        return workout.toDomain()
        */
        
        throw NetworkError.noData // Use appropriate local error or placeholder
    }
    
    public func isAvailable() async -> Bool {
        // TODO: Check SystemLanguageModel.default.availability
        return false
    }
}

// Note: FoundationModels is only available on A17 Pro/M1 or later and iOS 19+
