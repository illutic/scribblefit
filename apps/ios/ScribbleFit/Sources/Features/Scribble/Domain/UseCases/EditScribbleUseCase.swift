import Foundation
import CoreModel

@MainActor
public final class EditScribbleUseCase {
    private let repository: ScribbleRepository

    public init(repository: ScribbleRepository) {
        self.repository = repository
    }

    public func execute(id: UUID, newText: String) async throws {
        guard !newText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            throw NSError(domain: "ScribbleError", code: 1, userInfo: [NSLocalizedDescriptionKey: "Scribble text cannot be empty"])
        }

        guard var scribble = try await repository.getScribble(id: id) else {
            throw NSError(domain: "ScribbleError", code: 2, userInfo: [NSLocalizedDescriptionKey: "Scribble not found"])
        }

        scribble.rawText = newText
        scribble.status = .pending
        // Clear old exercises when editing
        try await repository.clearScribbleExercises(scribbleId: id)
        try await repository.updateScribble(scribble)
    }
}
