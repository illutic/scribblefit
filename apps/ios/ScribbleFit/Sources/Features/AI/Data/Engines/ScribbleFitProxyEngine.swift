import Foundation

public final class ScribbleFitProxyEngine: LLMEngine {
    private let client: ScribbleFitNetworkClient
    private let secureKeyStorage: SecureKeyStorage
    private let systemPrompt: String
    
    public init(client: ScribbleFitNetworkClient = .shared, secureKeyStorage: SecureKeyStorage, systemPrompt: String) {
        self.client = client
        self.secureKeyStorage = secureKeyStorage
        self.systemPrompt = systemPrompt
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        let token = try await secureKeyStorage.getAuthToken()
        let request = ParseRequest(rawText: rawText, prompt: systemPrompt)
        
        do {
            return try await client.parseProxy(request: request, token: token)
        } catch {
            throw AIParsingError(rawText = rawText, error: "Proxy Failure: \(error.localizedDescription)")
        }
    }
}
