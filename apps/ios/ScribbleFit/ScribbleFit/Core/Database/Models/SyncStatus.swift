import Foundation

public enum SyncStatus: String, Codable, CaseIterable {
    case pending = "PENDING"
    case processing = "PROCESSING"
    case failed = "FAILED"
    case completed = "COMPLETED"
}
