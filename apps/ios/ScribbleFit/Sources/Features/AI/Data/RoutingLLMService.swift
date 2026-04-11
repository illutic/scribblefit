import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

@MainActor
public final class RoutingLLMService: LLMService {
    private let configRepository: ConfigRepository
    private let localService: LLMService?
    private let geminiService: LLMService

    public init(configRepository: ConfigRepository, localService: LLMService?, geminiService: LLMService) {
        self.configRepository = configRepository
        self.localService = localService
        self.geminiService = geminiService
    }

    private var currentService: LLMService {
        let config = configRepository.getConfig()
        switch config.preferredLlmProvider {
        case .local:
            return localService ?? geminiService
        case .gemini:
            return geminiService
        }
    }

    public func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        try await currentService.parseWorkout(rawText: rawText)
    }

    public func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        try await currentService.generateInsightsSummary(exercises: exercises)
    }

    public func validateApiKey(_ apiKey: String) async throws {
        // Always use gemini service for validation if that's what we're testing
        try await geminiService.validateApiKey(apiKey)
    }

    public func getAvailableModels(apiKey: String) async throws -> [String] {
        try await geminiService.getAvailableModels(apiKey: apiKey)
    }
}
