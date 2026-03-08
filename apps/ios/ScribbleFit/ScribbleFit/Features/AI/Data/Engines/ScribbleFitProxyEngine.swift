import Foundation

public final class ScribbleFitProxyEngine: LLMEngine, AnalysisEngine {
    private let networkClient: ScribbleFitNetworkClient
    private let secureKeyStorage: SecureKeyStorage
    private let configRepository: ConfigRepository
    
    public init(
        networkClient: ScribbleFitNetworkClient,
        secureKeyStorage: SecureKeyStorage,
        configRepository: ConfigRepository
    ) {
        self.networkClient = networkClient
        self.secureKeyStorage = secureKeyStorage
        self.configRepository = configRepository
    }
    
    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        let startTime = Date()
        do {
            let token = try await secureKeyStorage.getAuthToken()
            let config = await configRepository.getConfig()
            let systemPrompt = config?.promptText ?? SystemConfig.defaultPrompt
            
            let request = ParseRequest(rawText: rawText, prompt: systemPrompt)
            let workout = try await networkClient.parseProxy(request: request, token: token)
            let duration = Int64(Date().timeIntervalSince(startTime) * 1000)
            
            return ParsedWorkoutResult(
                workout: workout,
                rawText: rawText,
                status: .success,
                modelUsed: "proxy-orchestrator",
                processingTimeMs: duration
            )
        } catch {
            let duration = Int64(Date().timeIntervalSince(startTime) * 1000)
            return ParsedWorkoutResult(
                workout: nil,
                rawText: rawText,
                status: .failure,
                modelUsed: "proxy-orchestrator",
                processingTimeMs: duration,
                error: error.localizedDescription
            )
        }
    }
    
    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        throw NSError(domain: "ScribbleFitProxyEngine", code: 501, userInfo: [NSLocalizedDescriptionKey: "Proxy analysis not yet supported by backend"])
    }
    
    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        throw NSError(domain: "ScribbleFitProxyEngine", code: 501, userInfo: [NSLocalizedDescriptionKey: "Proxy analysis not yet supported by backend"])
    }
    
    public func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight {
        throw NSError(domain: "ScribbleFitProxyEngine", code: 501, userInfo: [NSLocalizedDescriptionKey: "Proxy analysis not yet supported by backend"])
    }
}
