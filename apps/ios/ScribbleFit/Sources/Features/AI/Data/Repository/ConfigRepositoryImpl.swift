import Foundation
import SwiftData

@MainActor
public final class ConfigRepositoryImpl: ConfigRepository {
    private let networkClient: ScribbleFitNetworkClient
    private let database: ScribbleFitDatabase
    
    public init(networkClient: ScribbleFitNetworkClient = .shared, database: ScribbleFitDatabase = .shared) {
        self.networkClient = networkClient
        self.database = database
    }
    
    public func syncMetadata() async throws {
        let metadata = try await networkClient.getMetadata()
        let currentConfig = database.getConfig()
        
        if currentConfig == nil || currentConfig?.promptVersion != metadata.promptVersion {
            let promptConfig = try await networkClient.getPromptConfig()
            let newConfig = SystemConfig(
                id: "config",
                promptVersion: promptConfig.version,
                promptText: promptConfig.prompt,
                exerciseVersion: metadata.exerciseVersion,
                updatedAt: Date()
            )
            database.upsertConfig(newConfig)
        }
    }
    
    public func syncExercises() async throws {
        let metadata = try await networkClient.getMetadata()
        let currentConfig = database.getConfig()
        
        if currentConfig == nil || currentConfig?.exerciseVersion != metadata.exerciseVersion {
            let response = try await networkClient.getExercises()
            let entities = response.exercises.map { dto in
                ExerciseDictionary(
                    id: dto.id,
                    canonicalName: dto.canonicalName,
                    muscleGroup: dto.muscleGroup,
                    aliases: dto.aliases
                )
            }
            database.deleteAllExercises()
            database.upsertExercises(entities)
            
            // Update exercise version in config
            if let config = currentConfig {
                config.exerciseVersion = metadata.exerciseVersion
                config.updatedAt = Date()
                database.upsertConfig(config)
            } else {
                let newConfig = SystemConfig(
                    id: "config",
                    promptVersion: "0.0.0",
                    promptText: "",
                    exerciseVersion: metadata.exerciseVersion,
                    updatedAt: Date()
                )
                database.upsertConfig(newConfig)
            }
        }
    }
}
