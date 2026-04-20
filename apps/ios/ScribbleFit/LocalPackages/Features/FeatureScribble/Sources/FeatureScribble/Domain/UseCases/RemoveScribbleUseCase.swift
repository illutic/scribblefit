import Foundation
import CoreModel

@MainActor
public final class RemoveScribbleUseCase {
    private let repository: ScribbleRepository

    public init(repository: ScribbleRepository) {
        self.repository = repository
    }

    public func execute(id: UUID) async throws {
        // SwiftData with CASCADE handles clearing exercises
        try await repository.deleteScribble(id: id)
    }
}
