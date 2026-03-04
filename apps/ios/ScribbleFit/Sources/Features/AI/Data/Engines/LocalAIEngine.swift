import Foundation

#if canImport(FoundationModels)
import FoundationModels
#endif

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
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            // TODO: Finalize FoundationModels integration once SDK stability is confirmed
            // For now, we stub this to avoid complex Generable conformance errors
            throw NetworkError.noData
        }
        #endif
        
        throw NetworkError.noData
    }
    
    public func isAvailable() async -> Bool {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            return await SystemLanguageModel.default.availability == .available
        }
        #endif
        return false
    }
}
