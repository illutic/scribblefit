import Foundation
import CoreModel

@MainActor
public final class DeleteScribbleUseCase {
    private let repository: ScribbleRepository

    public init(repository: ScribbleRepository) {
        self.repository = repository
    }

    public func execute(id: UUID) async throws {
        try await repository.deleteScribble(id: id)
    }
}
