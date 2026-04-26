import Foundation
import SwiftData

@MainActor
public final class SettingsRepositoryImpl: SettingsRepository {
    private let database: ScribbleFitDatabase

    public init(database: ScribbleFitDatabase) {
        self.database = database
    }

    @MainActor
    public convenience init() {
        self.init(database: .shared)
    }

    public func getSettings() async throws -> AppSettings {
        let config = await database.getConfig()

        return AppSettings(
            aiProvider: LLMProvider(rawValue: config?.preferredLlmProvider ?? "gemini") ?? .gemini,
            weightUnit: WeightUnit(rawValue: config?.weightUnit ?? "lbs") ?? .lbs,
            themePreference: ThemePreference(rawValue: config?.themePreference ?? "system") ?? .system,
            selectedModel: config?.preferredModel.isEmpty == false ? config?.preferredModel : nil
        )
    }

    public func updateSettings(_ settings: AppSettings) async throws {
        let config = SystemConfig(
            preferredLlmProvider: settings.aiProvider.rawValue,
            preferredModel: settings.selectedModel ?? "",
            weightUnit: settings.weightUnit.rawValue,
            themePreference: settings.themePreference.rawValue,
            updatedAt: Date()
        )
        await database.upsertConfig(config)
    }

    public func clearAllData() async throws {
        await database.deleteAll()
    }
}
