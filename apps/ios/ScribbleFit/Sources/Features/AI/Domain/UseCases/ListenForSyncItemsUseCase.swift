import Foundation
import Combine

public final class ListenForSyncItemsUseCase: Sendable {
    private let syncRepository: any SyncRepository

    public init(syncRepository: any SyncRepository) {
        self.syncRepository = syncRepository
    }

    public func execute() async {
        for await items in syncRepository.observeAllSyncItems().values {
            if items.contains(where: { $0.status == .pending }) {
                try? await syncRepository.syncWorkouts()
            }
        }
    }
}
