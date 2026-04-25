import Foundation

@MainActor
public final class SyncWorkoutUseCase {
    private let syncRepository: SyncRepository
    private let engine: LLMEngine
    private let configRepository: ConfigRepository
    
    public init(
        syncRepository: SyncRepository,
        engine: LLMEngine,
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
            
            let result = await engine.parseWorkout(rawText: rawText)
            
            if result.status == .success, let workout = result.workout {
                try await syncRepository.saveParsedWorkout(syncItemId: item.id, workout: workout)
            } else {
                try await syncRepository.updateSyncStatus(id: item.id, status: .failed)
            }
        }
    }
}
