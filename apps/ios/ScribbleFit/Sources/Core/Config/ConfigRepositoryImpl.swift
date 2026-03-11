import Foundation

@MainActor
public final class ConfigRepositoryImpl: ConfigRepository {
    private let database: ScribbleFitDatabase

    public init(database: ScribbleFitDatabase) {
        self.database = database
    }

    public func getConfig() async -> SystemConfigDomain? {
        database.getConfig().map { entity in
            SystemConfigDomain(
                parsePrompt: entity.promptText,
                preferredLlmProvider: LLMProvider(rawValue: entity.preferredLlmProvider) ?? .proxy,
                preferredModel: entity.preferredModel.isEmpty ? nil : entity.preferredModel,
                weightUnit: Weight(rawValue: entity.weightUnit) ?? .lbs,
                themePreference: ThemePreference(rawValue: entity.themePreference) ?? .system,
                updatedAt: entity.updatedAt
            )
        }
    }

    public func updateConfig(_ config: SystemConfigDomain) async throws {
        let existing = database.getConfig()
        database.upsertConfig(SystemConfigEntity(
            id: "config",
            promptVersion: existing?.promptVersion ?? "1.0.0",
            promptText: config.parsePrompt,
            exerciseVersion: existing?.exerciseVersion ?? "0.0.0",
            preferredLlmProvider: config.preferredLlmProvider.rawValue,
            preferredModel: config.preferredModel ?? "",
            parsingMode: existing?.parsingMode ?? "managed",
            weightUnit: config.weightUnit.rawValue,
            themePreference: config.themePreference.rawValue,
            updatedAt: config.updatedAt
        ))
    }

    public func resetConfig() async {
        let defaultConfig = SystemConfigDomain()
        try? await updateConfig(defaultConfig)
    }
}
