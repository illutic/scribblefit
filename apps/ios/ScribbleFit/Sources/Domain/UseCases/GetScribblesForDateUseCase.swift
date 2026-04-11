import Foundation

@MainActor
public struct GetScribblesForDateUseCase: Sendable {
    private let repository: ScribbleRepository

    public init(repository: ScribbleRepository) {
        self.repository = repository
    }

    public func execute(date: Date) -> AsyncStream<[Scribble]> {
        repository.getScribbles(for: date)
    }
}
