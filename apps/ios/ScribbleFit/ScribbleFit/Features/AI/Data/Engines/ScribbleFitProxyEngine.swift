import Foundation

public final class ScribbleFitProxyEngine: LLMEngine, AnalysisEngine {
    private let networkClient: ScribbleFitNetworkClient
    private let secureKeyStorage: SecureKeyStorage
    private let systemPrompt: String
    
    public init(networkClient: ScribbleFitNetworkClient, secureKeyStorage: SecureKeyStorage, systemPrompt: String) {
        self.networkClient = networkClient
        self.secureKeyStorage = secureKeyStorage
        self.systemPrompt = systemPrompt
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        let token = try await secureKeyStorage.getAuthToken()
        let request = ParseRequest(rawText: rawText, prompt: systemPrompt)
        return try await networkClient.parseProxy(request: request, token: token)
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
