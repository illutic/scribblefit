import Foundation
import Combine

public enum AISyncStatus: String, Sendable {
    case pending = "PENDING"
    case processing = "PROCESSING"
    case completed = "COMPLETED"
    case failed = "FAILED"
}

public struct AISyncItem: Sendable {
    public let id: String
    public let itemType: String
    public let rawText: String?
    public let status: AISyncStatus
    public let jsonData: String?
    public let createdAt: Date

    public init(id: String, itemType: String, rawText: String?, status: AISyncStatus, jsonData: String?, createdAt: Date) {
        self.id = id
        self.itemType = itemType
        self.rawText = rawText
        self.status = status
        self.jsonData = jsonData
        self.createdAt = createdAt
    }
}

public protocol SyncRepository: Sendable {
    func observeAllSyncItems() -> AnyPublisher<[AISyncItem], Never>
    func getPendingSyncItems() async throws -> [AISyncItem]
    func getAllSyncItems() async throws -> [AISyncItem]
    func updateSyncStatus(id: String, status: AISyncStatus) async throws
    func saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) async throws
    func enqueueScribble(id: String, rawText: String) async throws
    func saveFeedItem(id: String, itemType: String, jsonData: String, status: AISyncStatus) async throws
    func deleteSyncItem(id: String) async throws
    func syncWorkouts() async throws
}
