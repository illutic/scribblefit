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
                promptVersion: entity.promptVersion,
                promptText: entity.promptText,
                exerciseVersion: entity.exerciseVersion,
                preferredLlmProvider: LLMProvider(rawValue: entity.preferredLlmProvider) ?? .proxy,
                preferredModel: entity.preferredModel,
                parsingMode: entity.parsingMode,
                weightUnit: entity.weightUnit,
                themePreference: entity.themePreference,
                updatedAt: entity.updatedAt
            )
        }
    }

    public func updateConfig(_ config: SystemConfigDomain) async throws {
        database.upsertConfig(SystemConfig(
            id: "config",
            promptVersion: config.promptVersion,
            promptText: config.promptText,
            exerciseVersion: config.exerciseVersion,
            preferredLlmProvider: config.preferredLlmProvider.rawValue,
            preferredModel: config.preferredModel,
            parsingMode: config.parsingMode,
            weightUnit: config.weightUnit,
            themePreference: config.themePreference,
            updatedAt: config.updatedAt
        ))
    }
}
