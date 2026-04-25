import Foundation
import CoreModel

@MainActor
public final class UpdateSystemConfigUseCase {
    private let repository: ConfigRepository

    public init(repository: ConfigRepository) {
        self.repository = repository
    }

    public func execute(config: SystemConfig) {
        repository.updateConfig(config)
    }
}
