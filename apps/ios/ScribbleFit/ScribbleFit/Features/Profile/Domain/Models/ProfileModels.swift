import Foundation

public struct UserStats: Sendable {
    public let totalWorkouts: Int
    public let lifetimeVolume: Double
    public let prCount: Int
    public let joinDate: Date

    public init(totalWorkouts: Int, lifetimeVolume: Double, prCount: Int, joinDate: Date) {
        self.totalWorkouts = totalWorkouts
        self.lifetimeVolume = lifetimeVolume
        self.prCount = prCount
        self.joinDate = joinDate
    }
}

public struct AppSettings: Sendable {
    public let aiProvider: LLMProvider
    public let weightUnit: WeightUnit
    public let themePreference: ThemePreference
    public let selectedModel: String?

    public init(
        aiProvider: LLMProvider,
        weightUnit: WeightUnit,
        themePreference: ThemePreference,
        selectedModel: String? = nil
    ) {
        self.aiProvider = aiProvider
        self.weightUnit = weightUnit
        self.themePreference = themePreference
        self.selectedModel = selectedModel
    }
}

public enum WeightUnit: String, Codable, Sendable {
    case lbs = "lbs"
    case kg = "kg"
}

public enum ThemePreference: String, Codable, Sendable {
    case light = "light"
    case dark = "dark"
    case system = "system"
}
