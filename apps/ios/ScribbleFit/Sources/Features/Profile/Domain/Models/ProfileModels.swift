import Foundation

public struct UserStats: Sendable {
    public let totalWorkouts: Int
    public let lifetimeVolume: Double
    public let prCount: Int
    public let joinDate: Date

    public init(totalWorkouts: Int, lifetimeVolume: Double, prCount: Int = 0, joinDate: Date) {
        self.totalWorkouts = totalWorkouts
        self.lifetimeVolume = lifetimeVolume
        self.prCount = prCount
        self.joinDate = joinDate
    }
}

public struct AppSettings: Sendable {
    public var aiProvider: LLMProvider
    public var weightUnit: WeightUnit
    public var themePreference: ThemePreference
    public var selectedModel: String?

    public init(aiProvider: LLMProvider = .proxy, weightUnit: WeightUnit = .lbs, themePreference: ThemePreference = .system, selectedModel: String? = nil) {
        self.aiProvider = aiProvider
        self.weightUnit = weightUnit
        self.themePreference = themePreference
        self.selectedModel = selectedModel
    }
}

public enum WeightUnit: String, Codable, Sendable { case lbs, kg }
public enum ThemePreference: String, Codable, Sendable { case light, dark, system }
