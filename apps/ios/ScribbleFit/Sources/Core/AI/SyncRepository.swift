import Combine
import Foundation

public protocol SyncRepository: Sendable {
    func getPendingSyncItems() async throws -> [AISyncItem]
    func getAllSyncItems() async throws -> [AISyncItem]
    func observeAllSyncItems() -> AnyPublisher<[AISyncItem], Never>
    func updateSyncStatus(id: String, status: SyncStatus) async throws
    func saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) async throws
    func enqueueScribble(id: String, rawText: String) async throws
    func saveFeedItem(id: String, type: String, jsonData: String, status: SyncStatus) async throws
    func deleteSyncItem(id: String) async throws
    func syncWorkouts() async throws
}
