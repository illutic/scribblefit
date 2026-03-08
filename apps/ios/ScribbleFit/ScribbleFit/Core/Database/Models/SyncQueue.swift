import Foundation
import SwiftData

@Model
public final class SyncQueue {
    @Attribute(.unique) public var id: String
    public var itemType: String
    public var rawText: String?
    public var status: String 
    public var jsonData: String?
    public var createdAt: Date
    
    public init(
        id: String = UUID().uuidString,
        itemType: String = "SCRIBBLE",
        rawText: String? = nil,
        status: SyncStatus = .pending,
        jsonData: String? = nil,
        createdAt: Date = Date()
    ) {
        self.id = id
        self.itemType = itemType
        self.rawText = rawText
        self.status = status.rawValue
        self.jsonData = jsonData
        self.createdAt = createdAt
    }
    
    public var syncStatus: SyncStatus {
        get { SyncStatus(rawValue: status) ?? .pending }
        set { status = newValue.rawValue }
    }
}
