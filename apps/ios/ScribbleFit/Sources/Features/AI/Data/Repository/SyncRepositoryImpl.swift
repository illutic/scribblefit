import Combine
import Foundation

@MainActor
public final class SyncRepositoryImpl: SyncRepository {
    private let database: ScribbleFitDatabase
    private var syncWorkoutUseCase: SyncWorkoutUseCase?
    nonisolated(unsafe) private let allItemsSubject = CurrentValueSubject<[AISyncItem], Never>([])

    public init(database: ScribbleFitDatabase) {
        self.database = database
        refreshAllItemsSubject()
    }

    public func configure(syncWorkoutUseCase: SyncWorkoutUseCase) {
        self.syncWorkoutUseCase = syncWorkoutUseCase
    }

    public nonisolated func observeAllSyncItems() -> AnyPublisher<[AISyncItem], Never> {
        allItemsSubject.eraseToAnyPublisher()
    }

    public func getPendingSyncItems() async throws -> [AISyncItem] {
        allItemsSubject.value.filter { $0.status == .pending }
    }

    public func getAllSyncItems() async throws -> [AISyncItem] {
        allItemsSubject.value
    }

    public func updateSyncStatus(id: String, status: SyncStatus) async throws {
        database.updateSyncStatus(id: id, status: status)
        refreshAllItemsSubject()
    }

    public func saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) async throws {
        let data = try JSONEncoder().encode(workout)
        guard let jsonString = String(data: data, encoding: .utf8) else { return }
        database.updateParsedResult(id: syncItemId, status: .completed, jsonData: jsonString)
        refreshAllItemsSubject()
    }

    public func enqueueScribble(id: String, rawText: String) async throws {
        let item = SyncQueue(id: id, itemType: "SCRIBBLE", rawText: rawText, status: .pending)
        database.insertSyncItem(item)
        refreshAllItemsSubject()
        await syncWorkouts()
    }

    public func saveFeedItem(id: String, type: String, jsonData: String, status: SyncStatus) async throws {
        let item = SyncQueue(id: id, itemType: type, status: status, jsonData: jsonData)
        database.insertSyncItem(item)
        refreshAllItemsSubject()
    }

    public func deleteSyncItem(id: String) async throws {
        database.deleteSyncItem(id: id)
        refreshAllItemsSubject()
    }

    public func syncWorkouts() async {
        guard let useCase = syncWorkoutUseCase else { return }
        await useCase.execute()
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
