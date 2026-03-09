import Combine
import Foundation

public final class CanvasRepositoryImpl: CanvasRepository {
    private let syncRepository: any SyncRepository
    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()

    public init(syncRepository: any SyncRepository) {
        self.syncRepository = syncRepository
    }

    public func getFeed() -> AnyPublisher<[FeedItem], Never> {
        syncRepository.observeAllSyncItems()
            .map { items in
                items.compactMap { item -> FeedItem? in
                    switch item.itemType {
                    case "SCRIBBLE":
                        if item.status == .completed,
                           let data = item.jsonData?.data(using: .utf8),
                           let workout = try? JSONDecoder().decode(ParsedWorkout.self, from: data) {
                            return .confirmation(ConfirmationItem(
                                id: item.id, timestamp: item.createdAt,
                                workout: workout, scribbleId: item.id
                            ))
                        }
                        return .scribble(ScribbleItem(
                            id: item.id, timestamp: item.createdAt,
                            rawText: item.rawText ?? "",
                            status: item.status.toScribbleStatus()
                        ))
                    default:
                        return nil
                    }
                }
                .sorted { $0.timestamp < $1.timestamp }
            }
            .eraseToAnyPublisher()
    }

    public func addScribble(rawText: String) async throws {
        try await syncRepository.enqueueScribble(id: UUID().uuidString, rawText: rawText)
    }

    public func retryScribble(id: String) async throws {
        try await syncRepository.updateSyncStatus(id: id, status: .pending)
        await syncRepository.syncWorkouts()
    }

    public func addConfirmation(item: ConfirmationItem) async throws {
        let data = try encoder.encode(item)
        let json = String(data: data, encoding: .utf8) ?? ""
        try await syncRepository.saveFeedItem(id: item.id, type: "CONFIRMATION", jsonData: json, status: .completed)
    }

    public func addInsight(item: InsightItem) async throws {
        let data = try encoder.encode(item)
        let json = String(data: data, encoding: .utf8) ?? ""
        try await syncRepository.saveFeedItem(id: item.id, type: "INSIGHT", jsonData: json, status: .completed)
    }

    public func removeFeedItem(id: String) async throws {
        try await syncRepository.deleteSyncItem(id: id)
    }

    public func clearFeed() async throws {}
}

private extension SyncStatus {
    func toScribbleStatus() -> ScribbleStatus {
        switch self {
        case .pending: return .pending
        case .processing: return .processing
        case .failed: return .failed
        case .completed: return .completed
        }
    }
}
