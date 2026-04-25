import Foundation
import CoreModel

@MainActor
public struct AddRawScribbleUseCase: Sendable {
    private let repository: ScribbleRepository

    public init(repository: ScribbleRepository) {
        self.repository = repository
    }

    public func execute(text: String, date: Date) async throws {
        let trimmedText = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmedText.isEmpty else { return }
        
        let scribble = Scribble(
            rawText: trimmedText,
            status: .pending,
            createdAt: date,
            exercises: []
        )
        try await repository.addScribble(scribble)
    }
}
