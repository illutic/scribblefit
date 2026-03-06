import Foundation
import SwiftData

@Model
public final class SyncQueue {
    @Attribute(.unique) public var id: String
    public var rawText: String
    public var status: String // Store as String to match Android or use Enum if SwiftData supports it well
    public var createdAt: Date
    
    public init(id: String = UUID().uuidString, rawText: String, status: SyncStatus = .pending, createdAt: Date = Date()) {
        self.id = id
        self.rawText = rawText
        self.status = status.rawValue
        self.createdAt = createdAt
    }
    
    public var syncStatus: SyncStatus {
        get { SyncStatus(rawValue: status) ?? .pending }
        set { status = newValue.rawValue }
    }
}
