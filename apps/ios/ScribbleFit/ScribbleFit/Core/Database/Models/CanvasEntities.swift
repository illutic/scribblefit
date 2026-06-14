import Foundation
import SwiftData

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
