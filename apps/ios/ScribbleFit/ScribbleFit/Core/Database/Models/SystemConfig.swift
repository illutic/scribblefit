import Foundation
import SwiftData

@Model
public final class SystemConfig: @unchecked Sendable {
    @Attribute(.unique) public var id: String
    public var promptVersion: String
    public var promptText: String
    public var exerciseVersion: String
    public var preferredLlmProvider: String
    public var parsingMode: String
    public var weightUnit: String
    public var themePreference: String
    public var updatedAt: Date
    
    public init(
        id: String = "config",
        promptVersion: String,
        promptText: String,
        exerciseVersion: String = "0.0.0",
        preferredLlmProvider: String = "proxy",
        parsingMode: String = "managed",
        weightUnit: String = "lbs",
        themePreference: String = "system",
        updatedAt: Date = Date()
    ) {
        self.id = id
        self.promptVersion = promptVersion
        self.promptText = promptText
        self.exerciseVersion = exerciseVersion
        self.preferredLlmProvider = preferredLlmProvider
        self.parsingMode = parsingMode
        self.weightUnit = weightUnit
        self.themePreference = themePreference
        self.updatedAt = updatedAt
    }
}
