import Foundation

public final class ModelRepositoryImpl: ModelRepository {
    private let session: URLSession

    public init(session: URLSession = .shared) {
        self.session = session
    }

    public func fetchModels(for provider: LLMProvider, apiKey: String) async throws -> [String] {
        switch provider {
        case .openai:
            return try await fetchOpenAIModels(apiKey: apiKey)
        case .gemini:
            return try await fetchGeminiModels(apiKey: apiKey)
        default:
            return []
        }
    }

    private func fetchOpenAIModels(apiKey: String) async throws -> [String] {
        let url = URL(string: "https://api.openai.com/v1/models")!
        var request = URLRequest(url: url)
        request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")

        let (data, response) = try await session.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode) else {
            throw URLError(.badServerResponse)
        }

        struct ModelList: Codable { let data: [ModelItem] }
        struct ModelItem: Codable { let id: String }

        let list = try JSONDecoder().decode(ModelList.self, from: data)
        return list.data
            .map { $0.id }
            .filter { $0.hasPrefix("gpt-") || $0.hasPrefix("o1") || $0.hasPrefix("o3") }
            .sorted()
    }

    private func fetchGeminiModels(apiKey: String) async throws -> [String] {
        let url = URL(string: "https://generativelanguage.googleapis.com/v1beta/models?key=\(apiKey)")!
        let (data, response) = try await session.data(from: url)
        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode) else {
            throw URLError(.badServerResponse)
        }

        struct ModelList: Codable { let models: [ModelItem] }
        struct ModelItem: Codable {
            let name: String
            let supportedGenerationMethods: [String]
        }

        let list = try JSONDecoder().decode(ModelList.self, from: data)
        return list.models
            .filter { $0.supportedGenerationMethods.contains("generateContent") }
            .map { $0.name }
            .sorted()
    }
}
