import Foundation
import SwiftData

@Model
public final class SystemConfig {
    @Attribute(.unique) public var id: String
    public var promptVersion: String
    public var promptText: String
    public var updatedAt: Date
    
    public init(id: String = "config", promptVersion: String, promptText: String, updatedAt: Date = Date()) {
        self.id = id
        self.promptVersion = promptVersion
        self.promptText = promptText
        self.updatedAt = updatedAt
    }
}
