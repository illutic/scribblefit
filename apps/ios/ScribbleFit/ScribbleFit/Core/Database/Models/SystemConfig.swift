import Foundation
import SwiftData
import CoreModel

@Model
public final class SystemConfig {
    @Attribute(.unique) public var id: String
    public var exerciseVersion: String
    public var preferredLlmProvider: String
    public var preferredModel: String
    public var weightUnit: String
    public var themePreference: String
    public var updatedAt: Date
    
    @Transient public var remoteConfig: CoreModel.RemoteConfig = CoreModel.RemoteConfig()

    public init(
        id: String = "config",
        exerciseVersion: String = "0.0.0",
        preferredLlmProvider: String = "gemini",
        preferredModel: String = "",
        weightUnit: String = "lbs",
        themePreference: String = "system",
        updatedAt: Date = Date()
    ) {
        self.id = id
        self.exerciseVersion = exerciseVersion
        self.preferredLlmProvider = preferredLlmProvider
        self.preferredModel = preferredModel
        self.weightUnit = weightUnit
        self.themePreference = themePreference
        self.updatedAt = updatedAt
    }
}
