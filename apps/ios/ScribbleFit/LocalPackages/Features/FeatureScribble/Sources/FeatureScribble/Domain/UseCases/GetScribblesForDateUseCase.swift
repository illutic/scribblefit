import Foundation
import CoreModel

@MainActor
public struct GetScribblesForDateUseCase {
    private let repository: ScribbleRepository

    public init(repository: ScribbleRepository) {
        self.repository = repository
    }

    public func execute(date: Date) -> AsyncStream<[Scribble]> {
        repository.observeScribbles(for: date)
    }
}
