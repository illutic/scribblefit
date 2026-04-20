import Foundation
import CoreModel

@MainActor
public final class RoutingLLMService: LLMService {
    private let geminiService: LLMService
    private let localService: LLMService
    private let configRepository: ConfigRepository
    
    public init(geminiService: LLMService, localService: LLMService, configRepository: ConfigRepository) {
        self.geminiService = geminiService
        self.localService = localService
        self.configRepository = configRepository
    }
    
    private var activeService: LLMService {
        get async {
            let config = configRepository.getConfig()
            switch config.preferredLlmProvider {
            case .gemini:
                return geminiService
            case .local:
                if await localService.isSupported() {
                    return localService
                } else {
                    return geminiService
                }
            }
        }
    }
    
    public func isSupported() async -> Bool {
        return true
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        return try await activeService.parseWorkout(rawText: rawText)
    }
    
    public func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        return try await activeService.generateInsightsSummary(exercises: exercises)
    }
}
