import Foundation

@MainActor
public protocol ScribbleRepository: Sendable {
    func getScribbles(for date: Date) -> AsyncStream<[Scribble]>
    func addScribble(_ scribble: Scribble) async throws
    func updateScribble(_ scribble: Scribble) async throws
    func deleteScribble(id: UUID) async throws
    func getScribble(id: UUID) async throws -> Scribble?
    func clearScribbleExercises(scribbleId: UUID) async throws
}

public enum ScribbleError: Error {
    case emptyText
    case notFound(UUID)
    case parsingFailed(Error?)
    case persistenceFailed(Error?)
}
