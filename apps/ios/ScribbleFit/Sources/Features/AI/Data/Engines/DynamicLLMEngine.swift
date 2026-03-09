import Foundation

public final class DynamicLLMEngine: LLMEngine, Sendable {
    private let configRepository: any ConfigRepository
    private let geminiEngine: GeminiAIEngine
    private let openAIEngine: OpenAIAIEngine
    private let localEngine: LocalAIEngine
    private let proxyEngine: ScribbleFitProxyEngine

    public init(
        configRepository: any ConfigRepository,
        geminiEngine: GeminiAIEngine,
        openAIEngine: OpenAIAIEngine,
        localEngine: LocalAIEngine,
        proxyEngine: ScribbleFitProxyEngine
    ) {
        self.configRepository = configRepository
        self.geminiEngine = geminiEngine
        self.openAIEngine = openAIEngine
        self.localEngine = localEngine
        self.proxyEngine = proxyEngine
    }

    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        let config = await configRepository.getConfig()
        switch config?.preferredLlmProvider ?? .proxy {
        case .openai: return await openAIEngine.parseWorkout(rawText: rawText)
        case .gemini: return await geminiEngine.parseWorkout(rawText: rawText)
        case .local: return await localEngine.parseWorkout(rawText: rawText)
        case .proxy: return await proxyEngine.parseWorkout(rawText: rawText)
        }
    }
}
