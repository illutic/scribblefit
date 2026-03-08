import Foundation

public final class SyncWorkoutUseCase: Sendable {
    private let syncRepository: any SyncRepository
    private let engine: any LLMEngine

    public init(syncRepository: any SyncRepository, engine: any LLMEngine) {
        self.syncRepository = syncRepository
        self.engine = engine
    }

    public func execute() async {
        guard let pendingItems = try? await syncRepository.getPendingSyncItems() else { return }
        for item in pendingItems {
            guard let rawText = item.rawText else { continue }
            try? await syncRepository.updateSyncStatus(id: item.id, status: .processing)
            let result = await engine.parseWorkout(rawText: rawText)
            if result.status == .success, let workout = result.workout {
                try? await syncRepository.saveParsedWorkout(syncItemId: item.id, workout: workout)
            } else {
                try? await syncRepository.updateSyncStatus(id: item.id, status: .failed)
            }
        }
    }
}
