import Foundation

public final class ScribbleFitProxyEngine: LLMEngine {
    private let client: ScribbleFitNetworkClient
    private let systemPrompt: String
    
    public init(client: ScribbleFitNetworkClient = .shared, systemPrompt: String) {
        self.client = client
        self.systemPrompt = systemPrompt
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        let request = ParseRequest(rawText: rawText, prompt: systemPrompt)
        return try await client.parseProxy(request: request)
    }
}
