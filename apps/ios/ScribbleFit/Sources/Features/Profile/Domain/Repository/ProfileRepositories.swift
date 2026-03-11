import Foundation

public protocol UserRepository: Sendable {
    func getUserStats() async throws -> UserStats
}

public protocol ModelRepository: Sendable {
    func fetchModels(for provider: LLMProvider, apiKey: String) async throws -> [String]
}
