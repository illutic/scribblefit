import Foundation

@MainActor
public final class UpdateApiKeyUseCase {
    private let repository: SettingsRepository

    public init(repository: SettingsRepository) {
        self.repository = repository
    }

    public func execute(apiKey: String) async throws {
        try await repository.saveApiKey(apiKey)
    }
}
