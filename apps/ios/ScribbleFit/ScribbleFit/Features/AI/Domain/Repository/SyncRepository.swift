import Foundation

public enum AISyncStatus: String, Sendable {
    case pending = "PENDING"
    case processing = "PROCESSING"
    case completed = "COMPLETED"
    case failed = "FAILED"
}

public struct AISyncItem: Sendable {
    public let id: String
    public let rawText: String
    public let status: AISyncStatus
    public let createdAt: Date
    
    public init(id: String, rawText: String, status: AISyncStatus, createdAt: Date) {
        self.id = id
        self.rawText = rawText
        self.status = status
        self.createdAt = createdAt
    }
}

public protocol SyncRepository: Sendable {
    func getPendingSyncItems() async throws -> [AISyncItem]
    func updateSyncStatus(id: String, status: AISyncStatus) async throws
    func saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) async throws
    func enqueueScribble(rawText: String) async throws
}
