import Foundation
import SwiftData

public final class ConfigRepositoryImpl: ConfigRepository {
    private let database: ScribbleFitDatabase
    
    public init(database: ScribbleFitDatabase) {
        self.database = database
    }
    
    @MainActor
    public convenience init() {
        self.init(database: .shared)
    }
    
    public func getConfig() async -> SystemConfig? {
        await database.getConfig()
    }
    
    public func updateConfig(_ config: SystemConfig) async {
        await database.upsertConfig(config)
    }
    
    public func syncMetadata() async throws {
        // No-op, network removed
    }
}
