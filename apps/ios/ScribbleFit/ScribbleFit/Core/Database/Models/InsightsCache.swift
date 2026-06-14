import Foundation
import SwiftData

@Model
public final class InsightsCache {
    @Attribute(.unique) public var key: String
    public var jsonData: String
    public var createdAt: Date

    public init(key: String, jsonData: String, createdAt: Date = Date()) {
        self.key = key
        self.jsonData = jsonData
        self.createdAt = createdAt
    }
}
