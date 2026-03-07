import Foundation
import SwiftData

public final class SettingsRepositoryImpl: SettingsRepository {
    private let database: ScribbleFitDatabase
    
    public init(database: ScribbleFitDatabase = .shared) {
        self.database = database
    }
    
    public func getSettings() async throws -> AppSettings {
        let config = database.getSystemConfig()
        
        return AppSettings(
            parsingMode: ParsingMode(rawValue: config?.parsingMode ?? "managed") ?? .managed,
            aiProvider: LLMProvider(rawValue: config?.preferredLlmProvider ?? "proxy") ?? .proxy,
            weightUnit: WeightUnit(rawValue: config?.weightUnit ?? "lbs") ?? .lbs,
            themePreference: ThemePreference(rawValue: config?.themePreference ?? "system") ?? .system
        )
    }
    
    public func updateSettings(_ settings: AppSettings) async throws {
        let config = SystemConfig(
            promptVersion: "1.0.0",
            promptText: "",
            preferredLlmProvider: settings.aiProvider.rawValue,
            parsingMode: settings.parsingMode.rawValue,
            weightUnit: settings.weightUnit.rawValue,
            themePreference: settings.themePreference.rawValue,
            updatedAt: Date()
        )
        database.upsertSystemConfig(config)
    }
    
    public func clearAllData() async throws {
        database.deleteAll()
    }
}
