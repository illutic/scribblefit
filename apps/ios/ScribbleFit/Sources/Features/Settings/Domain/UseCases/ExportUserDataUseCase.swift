import Foundation
import CoreModel

@MainActor
public final class ExportUserDataUseCase {
    private let repository: SettingsRepository

    public init(repository: SettingsRepository) {
        self.repository = repository
    }

    public func execute() async throws -> URL {
        return try await repository.exportUserData()
    }
}
