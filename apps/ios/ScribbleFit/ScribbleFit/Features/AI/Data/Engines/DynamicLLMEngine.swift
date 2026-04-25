import Foundation
import CoreModel

/**
 * Orchestrator engine that implements dynamic fallback logic between different LLM providers.
 */
@MainActor
public final class DynamicLLMEngine: LLMService {
    private let geminiService: LLMService
    private let localService: LLMService
    private let configRepository: ConfigRepository
    
    public init(
        geminiService: LLMService,
        localService: LLMService,
        configRepository: ConfigRepository
    ) {
        self.geminiService = geminiService
        self.localService = localService
        self.configRepository = configRepository
    }
    
    public func isSupported() async -> Bool {
        return true
    }

    public func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        let config = await configRepository.getConfig()
        let preferredString = config?.preferredLlmProvider ?? "gemini"
        let preferred = LLMProvider(rawValue: preferredString) ?? .gemini
        
        let services = getServicePriorityList(preferred: preferred)
        var lastError: Error?
        
        for service in services {
            do {
                return try await service.parseWorkout(rawText: rawText)
            } catch {
                lastError = error
                continue
            }
        }
        
        throw lastError ?? NSError(domain: "DynamicLLMEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "No engines available or all engines failed"])
    }
    
    public func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        let config = await configRepository.getConfig()
        let preferredString = config?.preferredLlmProvider ?? "gemini"
        let preferred = LLMProvider(rawValue: preferredString) ?? .gemini
        let services = getServicePriorityList(preferred: preferred)
        
        var lastError: Error?
        for service in services {
            do {
                return try await service.generateInsightsSummary(exercises: exercises)
            } catch {
                lastError = error
            }
        }
        throw lastError ?? NSError(domain: "DynamicLLMEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "All analysis engines failed"])
    }
    
    public func generateExerciseInsight(history: String) async throws -> AIInsight {
        let config = await configRepository.getConfig()
        let preferredString = config?.preferredLlmProvider ?? "gemini"
        let preferred = LLMProvider(rawValue: preferredString) ?? .gemini
        let services = getServicePriorityList(preferred: preferred)
        
        var lastError: Error?
        for service in services {
            do {
                return try await service.generateExerciseInsight(history: history)
            } catch {
                lastError = error
            }
        }
        throw lastError ?? NSError(domain: "DynamicLLMEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "All analysis engines failed"])
    }
    
    private func getServicePriorityList(preferred: LLMProvider) -> [LLMService] {
        switch preferred {
        case .gemini: return [geminiService, localService]
        case .local: return [localService, geminiService]
        }
    }
}
