import Foundation
import SwiftData

public enum SyncStatus: String, Codable, Sendable {
    case pending = "PENDING"
    case processing = "PROCESSING"
    case completed = "COMPLETED"
    case failed = "FAILED"
}

@Model public final class ScribbleEntity {
    public var id: String
    public var itemType: String
    public var rawText: String?
    public var status: String
    public var jsonData: String?
    public var createdAt: Date

    public var syncStatus: SyncStatus {
        get { SyncStatus(rawValue: status) ?? .pending }
        set { status = newValue.rawValue }
    }

    public init(id: String, itemType: String, rawText: String? = nil, status: SyncStatus = .pending, jsonData: String? = nil, createdAt: Date = Date()) {
        self.id = id
        self.itemType = itemType
        self.rawText = rawText
        self.status = status.rawValue
        self.jsonData = jsonData
        self.createdAt = createdAt
    }
}
