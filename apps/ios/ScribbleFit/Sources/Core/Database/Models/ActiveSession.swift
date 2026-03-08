import Foundation
import SwiftData

@Model public final class ActiveSession {
    public var id: String
    public var jsonData: String
    public var updatedAt: Date

    public init(id: String = "active_session", jsonData: String, updatedAt: Date = Date()) {
        self.id = id
        self.jsonData = jsonData
        self.updatedAt = updatedAt
    }
}
