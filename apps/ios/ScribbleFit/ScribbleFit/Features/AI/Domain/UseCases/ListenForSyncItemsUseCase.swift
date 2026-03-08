import Foundation
internal import Combine

/**
 * Listens for changes to all sync items and triggers syncWorkouts() whenever
 * pending items are present — mirroring Android's ListenForSyncItemsUseCase.
 */
public final class ListenForSyncItemsUseCase {
    private let syncRepository: SyncRepository

    public init(syncRepository: SyncRepository) {
        self.syncRepository = syncRepository
    }

    public func execute() async {
        for await items in syncRepository.observeAllSyncItems().values {
            let hasPending = items.contains { $0.status == .pending }
            guard hasPending else { continue }
            try? await syncRepository.syncWorkouts()
        }
    }
}
