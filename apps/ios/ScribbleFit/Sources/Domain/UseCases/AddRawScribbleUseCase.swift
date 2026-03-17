import Foundation

@MainActor
public struct AddRawScribbleUseCase: Sendable {
    private let repository: ScribbleRepository

    public init(repository: ScribbleRepository) {
        self.repository = repository
    }

    public func execute(text: String, date: Date) async throws {
        guard !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return }
        
        let scribble = Scribble(
            rawText: text,
            status: .raw,
            createdAt: date,
            exercises: []
        )
        try await repository.addScribble(scribble)
    }
}
