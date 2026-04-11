import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

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
