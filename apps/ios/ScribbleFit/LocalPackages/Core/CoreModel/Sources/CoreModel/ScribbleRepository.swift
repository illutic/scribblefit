import Foundation

@MainActor
public protocol ScribbleRepository: Sendable {
    func observeScribbles(for date: Date) -> AsyncStream<[Scribble]>
    func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]>
    func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]>
    func addScribble(_ scribble: Scribble) async throws
    func updateScribble(_ scribble: Scribble) async throws
    func deleteScribble(id: UUID) async throws
    func getScribble(id: UUID) async throws -> Scribble?
    func clearScribbleExercises(scribbleId: UUID) async throws
    func confirmScribble(_ scribble: Scribble) async throws
}

public enum ScribbleError: Error {
    case emptyText
    case notFound(UUID)
    case parsingFailed(Error?)
    case persistenceFailed(Error?)
    case invalidStatus(ScribbleStatus)
}
