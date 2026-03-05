import Foundation
import SwiftData

@Model
public final class SystemConfig {
    @Attribute(.unique) public var id: String
    public var promptVersion: String
    public var promptText: String
    public var exerciseVersion: String
    public var updatedAt: Date
    
    public init(id: String = "config", promptVersion: String, promptText: String, exerciseVersion: String = "0.0.0", updatedAt: Date = Date()) {
        self.id = id
        self.promptVersion = promptVersion
        self.promptText = promptText
        self.exerciseVersion = exerciseVersion
        self.updatedAt = updatedAt
    }
}
