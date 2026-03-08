import Foundation

@MainActor
public final class SettingsRepositoryImpl: SettingsRepository {
    private let database: ScribbleFitDatabase

    public init(database: ScribbleFitDatabase) {
        self.database = database
    }

    public func getSettings() async throws -> AppSettings {
        guard let entity = database.getConfig() else { return AppSettings() }
        return AppSettings(
            aiProvider: LLMProvider(rawValue: entity.preferredLlmProvider) ?? .proxy,
            weightUnit: WeightUnit(rawValue: entity.weightUnit) ?? .lbs,
            themePreference: ThemePreference(rawValue: entity.themePreference) ?? .system,
            selectedModel: entity.preferredModel.isEmpty ? nil : entity.preferredModel
        )
    }

    public func updateSettings(_ settings: AppSettings) async throws {
        let existing = database.getConfig()
        database.upsertConfig(SystemConfig(
            id: "config",
            promptVersion: existing?.promptVersion ?? "1.0.0",
            promptText: existing?.promptText ?? SystemConfig.defaultPrompt,
            exerciseVersion: existing?.exerciseVersion ?? "0.0.0",
            preferredLlmProvider: settings.aiProvider.rawValue,
            preferredModel: settings.selectedModel ?? "",
            weightUnit: settings.weightUnit.rawValue,
            themePreference: settings.themePreference.rawValue,
            updatedAt: Date()
        ))
    }

    public func clearAllData() async throws {
        database.deleteAll()
    }
}
