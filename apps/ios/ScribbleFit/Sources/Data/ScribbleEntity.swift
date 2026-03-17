import Foundation
import SwiftData

@Model
public final class ScribbleEntity {
    @Attribute(.unique) public var id: UUID
    public var rawText: String
    public var parsedJson: String?
    public var status: String
    public var createdAt: Date
    
    public init(id: UUID, rawText: String, parsedJson: String?, status: String, createdAt: Date) {
        self.id = id
        self.rawText = rawText
        self.parsedJson = parsedJson
        self.status = status
        self.createdAt = createdAt
    }
}
