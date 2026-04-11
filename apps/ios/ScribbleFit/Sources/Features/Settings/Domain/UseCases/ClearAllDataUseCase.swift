import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

@MainActor
public final class ClearAllDataUseCase {
    private let repository: SettingsRepository

    public init(repository: SettingsRepository) {
        self.repository = repository
    }

    public func execute() async throws {
        try await repository.clearAllData()
    }
}
