import Combine
import Foundation

@MainActor
public final class SyncRepositoryImpl {
    private let database: ScribbleFitDatabase
    nonisolated(unsafe) private let allItemsSubject = CurrentValueSubject<[AISyncItem], Never>([])

    public init(database: ScribbleFitDatabase) {
        self.database = database
        refreshAllItemsSubject()
    }

    public nonisolated func observeAllSyncItems() -> AnyPublisher<[AISyncItem], Never> {
        allItemsSubject.eraseToAnyPublisher()
    }

    public func getPendingSyncItems() -> [AISyncItem] {
        allItemsSubject.value.filter { $0.status == .pending }
    }

    public func getAllSyncItems() -> [AISyncItem] {
        allItemsSubject.value
    }

    public func updateSyncStatus(id: String, status: SyncStatus) {
        database.updateSyncStatus(id: id, status: status)
        refreshAllItemsSubject()
    }

    public func saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) throws {
        let data = try JSONEncoder().encode(workout)
        guard let jsonString = String(data: data, encoding: .utf8) else { return }
        database.updateParsedResult(id: syncItemId, status: .completed, jsonData: jsonString)
        refreshAllItemsSubject()
    }

    public func enqueueScribble(id: String, rawText: String) {
        let item = ScribbleEntity(id: id, itemType: "SCRIBBLE", rawText: rawText, status: .pending)
        database.insertSyncItem(item)
        refreshAllItemsSubject()
    }

    public func saveFeedItem(id: String, type: String, jsonData: String, status: SyncStatus) {
        let item = ScribbleEntity(id: id, itemType: type, status: status, jsonData: jsonData)
        database.insertSyncItem(item)
        refreshAllItemsSubject()
    }

    public func deleteSyncItem(id: String) {
        database.deleteSyncItem(id: id)
        refreshAllItemsSubject()
    }

    private func refreshAllItemsSubject() {
        let items = database.getAllSyncItems().map { q in
            AISyncItem(
                id: q.id,
                itemType: q.itemType,
                rawText: q.rawText,
                status: q.syncStatus,
                createdAt: q.createdAt,
                jsonData: q.jsonData
            )
        }
        allItemsSubject.send(items)
    }
}
