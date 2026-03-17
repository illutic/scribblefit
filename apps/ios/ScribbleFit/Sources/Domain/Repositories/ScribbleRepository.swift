import Foundation

@MainActor
public protocol ScribbleRepository: Sendable {
    func getScribbles(for date: Date) -> AsyncStream<[Scribble]>
    func addScribble(_ scribble: Scribble) async throws
    func updateScribble(_ scribble: Scribble) async throws
    func deleteScribble(id: UUID) async throws
    func getScribble(id: UUID) async throws -> Scribble?
}
