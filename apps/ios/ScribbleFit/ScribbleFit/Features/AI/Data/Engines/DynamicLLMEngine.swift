import Foundation

/**
 * Orchestrator engine that implements dynamic fallback logic between different LLM providers.
 */
public final class DynamicLLMEngine: LLMEngine, AnalysisEngine {
    private let openAIEngine: LLMEngine
    private let geminiAIEngine: LLMEngine
    private let proxyEngine: LLMEngine
    private let localAIEngine: LocalAIEngine
    private let configRepository: ConfigRepository
    
    public init(
        openAIEngine: LLMEngine,
        geminiAIEngine: LLMEngine,
        proxyEngine: LLMEngine,
        localAIEngine: LocalAIEngine,
        configRepository: ConfigRepository
    ) {
        self.openAIEngine = openAIEngine
        self.geminiAIEngine = geminiAIEngine
        self.proxyEngine = proxyEngine
        self.localAIEngine = localAIEngine
        self.configRepository = configRepository
    }
    
    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        let config = await configRepository.getConfig()
        let preferredString = config?.preferredLlmProvider ?? "proxy"
        let preferred = LLMProvider(rawValue: preferredString) ?? .proxy
        
        let engines = getEnginePriorityList(preferred: preferred)
        var lastResult: ParsedWorkoutResult?
        
        for engine in engines {
            let result = await engine.parseWorkout(rawText: rawText)
            if result.status == .success {
                return result
            }
            lastResult = result
        }
        
        return lastResult ?? ParsedWorkoutResult(
            workout: nil,
            rawText: rawText,
            status: .failure,
            error: "No engines available or all engines failed"
        )
    }
    
    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        let config = await configRepository.getConfig()
        let preferredString = config?.preferredLlmProvider ?? "proxy"
        let preferred = LLMProvider(rawValue: preferredString) ?? .proxy
        let engines = getAnalysisEnginePriorityList(preferred: preferred)
        
        var lastError: Error?
        for engine in engines {
            do {
                return try await engine.generateSuggestion(context: context)
            } catch {
                lastError = error
            }
        }
        throw lastError ?? NSError(domain: "DynamicLLMEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "All analysis engines failed"])
    }
    
    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        let config = await configRepository.getConfig()
        let preferredString = config?.preferredLlmProvider ?? "proxy"
        let preferred = LLMProvider(rawValue: preferredString) ?? .proxy
        let engines = getAnalysisEnginePriorityList(preferred: preferred)
        
        var lastError: Error?
        for engine in engines {
            do {
                return try await engine.generateSummary(period: period, workoutData: workoutData)
            } catch {
                lastError = error
            }
        }
        throw lastError ?? NSError(domain: "DynamicLLMEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "All analysis engines failed"])
    }
    
    public func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight {
        let config = await configRepository.getConfig()
        let preferredString = config?.preferredLlmProvider ?? "proxy"
        let preferred = LLMProvider(rawValue: preferredString) ?? .proxy
        let engines = getAnalysisEnginePriorityList(preferred: preferred)
        
        var lastError: Error?
        for engine in engines {
            do {
                return try await engine.generateExerciseInsight(exerciseName: exerciseName, historyData: historyData)
            } catch {
                lastError = error
            }
        }
        throw lastError ?? NSError(domain: "DynamicLLMEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "All analysis engines failed"])
    }
    
    private func getEnginePriorityList(preferred: LLMProvider) -> [LLMEngine] {
        switch preferred {
        case .openai: return [openAIEngine, geminiAIEngine, proxyEngine, localAIEngine]
        case .gemini: return [geminiAIEngine, openAIEngine, proxyEngine, localAIEngine]
        case .proxy: return [proxyEngine, geminiAIEngine, openAIEngine, localAIEngine]
        case .local: return [localAIEngine, geminiAIEngine, openAIEngine, proxyEngine]
        }
    }
    
    private func getAnalysisEnginePriorityList(preferred: LLMProvider) -> [AnalysisEngine] {
        return getEnginePriorityList(preferred: preferred).compactMap { $0 as? AnalysisEngine }
    }
}
