import Foundation

public final class ModelRepositoryImpl: ModelRepository {
    private let session: URLSession

    public init(session: URLSession = .shared) {
        self.session = session
    }

    public func fetchModels(for provider: LLMProvider, apiKey: String) async throws -> [String] {
        switch provider {
        case .gemini:
            return try await fetchGeminiModels(apiKey: apiKey)
        case .local:
            return []
        }
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
