import Foundation
import SwiftData

public final class ConfigRepositoryImpl: ConfigRepository {
    private let networkClient: ScribbleFitNetworkClient
    private let database: ScribbleFitDatabase
    
    public init(networkClient: ScribbleFitNetworkClient, database: ScribbleFitDatabase) {
        self.networkClient = networkClient
        self.database = database
    }
    
    @MainActor
    public convenience init() {
        self.init(networkClient: .shared, database: .shared)
    }
    
    public func getConfig() async -> SystemConfig? {
        await database.getConfig()
    }
    
    public func updateConfig(_ config: SystemConfig) async {
        await database.upsertConfig(config)
    }
    
    public func syncMetadata() async throws {
        let metadata = try await networkClient.getMetadata()
        let currentConfig = await getConfig()
        
        if currentConfig == nil || currentConfig?.promptVersion != metadata.promptVersion {
            let promptConfig = try await networkClient.getPromptConfig()
            let newConfig = SystemConfig(
                id: "config",
                promptVersion: promptConfig.version,
                promptText: promptConfig.prompt,
                exerciseVersion: metadata.exerciseVersion,
                updatedAt: Date()
            )
            await updateConfig(newConfig)
        }
    }
    
}


