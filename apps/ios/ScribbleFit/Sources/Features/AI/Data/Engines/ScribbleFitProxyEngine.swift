import Foundation

public final class ScribbleFitProxyEngine: LLMEngine {
    private let networkClient: ScribbleFitNetworkClient
    private let prompt: String

    public init(networkClient: ScribbleFitNetworkClient, prompt: String) {
        self.networkClient = networkClient
        self.prompt = prompt
    }

    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        ParsedWorkoutResult(
            workout: nil,
            rawText: rawText,
            status: .failure,
            error: "Proxy engine not yet configured"
        )
    }
}
