import Combine
import Foundation

@MainActor
public final class ScribbleRepositoryImpl: ScribbleRepository {
    private let database: ScribbleFitDatabase
    nonisolated(unsafe) private let allScribblesSubject = CurrentValueSubject<[Scribble], Never>([])

    public init(database: ScribbleFitDatabase) {
        self.database = database
        refresh()
    }

    public nonisolated func getAllScribbles() -> AnyPublisher<[Scribble], Never> {
        allScribblesSubject.eraseToAnyPublisher()
    }

    public nonisolated func getPendingScribbles() -> AnyPublisher<[Scribble], Never> {
        allScribblesSubject
            .map { scribbles in
                scribbles.filter { scribble in
                    if case .raw(_, _, _, let status) = scribble {
                        return status == .pending
                    }
                    return false
                }
            }
            .eraseToAnyPublisher()
    }

    public func updateSyncStatus(id: String, status: SyncStatus) async throws {
        database.updateSyncStatus(id: id, status: status)
        refresh()
    }

    public func enqueueScribble(rawText: String) async {
        let item = ScribbleEntity(id: UUID().uuidString, itemType: "SCRIBBLE", rawText: rawText, status: .pending)
        database.insertSyncItem(item)
        refresh()
    }

    private func refresh() {
        let scribbles = database.getAllSyncItems()
            .filter { $0.itemType == "SCRIBBLE" }
            .map { $0.toScribble() }
        allScribblesSubject.send(scribbles)
    }
}
