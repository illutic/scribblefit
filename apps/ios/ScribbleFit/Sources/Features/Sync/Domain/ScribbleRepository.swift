import Combine
import Foundation

public protocol ScribbleRepository: Sendable {
    func getAllScribbles() -> AnyPublisher<[Scribble], Never>
    func getPendingScribbles() -> AnyPublisher<[Scribble], Never>
    func updateSyncStatus(id: String, status: SyncStatus) async throws
    func enqueueScribble(rawText: String) async
}
