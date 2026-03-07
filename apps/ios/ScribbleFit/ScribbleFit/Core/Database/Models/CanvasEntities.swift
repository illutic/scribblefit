import Foundation
import SwiftData

/**
 * Persists the state of the Intelligent Canvas feed to allow recovery after app kill.
 */
@Model
public final class CanvasFeed {
    @Attribute(.unique) public var id: String
    public var itemType: String
    public var jsonData: String
    public var createdAt: Date
    
    public init(id: String, itemType: String, jsonData: String, createdAt: Date) {
        self.id = id
        self.itemType = itemType
        self.jsonData = jsonData
        self.createdAt = createdAt
    }
}

/**
 * Persists the current uncommitted workout session.
 */
@Model
public final class ActiveSession {
    @Attribute(.unique) public var id: String = "current_session"
    public var jsonData: String
    public var updatedAt: Date
    
    public init(id: String = "current_session", jsonData: String, updatedAt: Date) {
        self.id = id
        self.jsonData = jsonData
        self.updatedAt = updatedAt
    }
}
