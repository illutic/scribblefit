import Foundation

public final class ModelRepositoryImpl: ModelRepository {
    private let networkClient: ScribbleFitNetworkClient

    public init(networkClient: ScribbleFitNetworkClient) {
        self.networkClient = networkClient
    }

    public func fetchModels(for provider: LLMProvider, apiKey: String) async throws -> [String] {
        switch provider {
        case .openai: return try await fetchOpenAIModels(apiKey: apiKey)
        case .gemini: return try await fetchGeminiModels(apiKey: apiKey)
        default: return []
        }
    }

    private func fetchOpenAIModels(apiKey: String) async throws -> [String] {
        struct ModelData: Decodable { let id: String }
        struct ModelsResponse: Decodable { let data: [ModelData] }
        guard let url = URL(string: "https://api.openai.com/v1/models") else { return [] }
        let response: ModelsResponse = try await networkClient.get(url: url, headers: ["Authorization": "Bearer \(apiKey)"])
        return response.data.map(\.id).filter { $0.hasPrefix("gpt-") || $0.hasPrefix("o1") || $0.hasPrefix("o3") }.sorted()
    }

    private func fetchGeminiModels(apiKey: String) async throws -> [String] {
        struct GeminiModel: Decodable { let name: String; let supportedGenerationMethods: [String] }
        struct GeminiModelsResponse: Decodable { let models: [GeminiModel] }
        guard let url = URL(string: "https://generativelanguage.googleapis.com/v1beta/models?key=\(apiKey)") else { return [] }
        let response: GeminiModelsResponse = try await networkClient.get(url: url)
        return response.models.filter { $0.supportedGenerationMethods.contains("generateContent") }.map { $0.name.replacingOccurrences(of: "models/", with: "") }.sorted()
    }
}
