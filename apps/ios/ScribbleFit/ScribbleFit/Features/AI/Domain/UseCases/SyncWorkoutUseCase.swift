import Foundation

@MainActor
public final class SyncWorkoutUseCase {
    private let syncRepository: SyncRepository
    private let engine: LLMService
    private let configRepository: ConfigRepository
    
    public init(
        syncRepository: SyncRepository,
        engine: LLMService,
        configRepository: ConfigRepository
    ) {
        self.syncRepository = syncRepository
        self.engine = engine
        self.configRepository = configRepository
    }
    
    public func execute() async throws {
        let pendingItems = try await syncRepository.getPendingSyncItems()
        
        for item in pendingItems {
            guard let rawText = item.rawText else { continue }
            try await syncRepository.updateSyncStatus(id: item.id, status: .processing)
            
            do {
                let result = try await engine.parseWorkout(rawText: rawText)
                try await syncRepository.saveParsedWorkout(syncItemId: item.id, workout: result.exercises)
            } catch {
                try await syncRepository.updateSyncStatus(id: item.id, status: .failed)
            }
        }
    }
}
