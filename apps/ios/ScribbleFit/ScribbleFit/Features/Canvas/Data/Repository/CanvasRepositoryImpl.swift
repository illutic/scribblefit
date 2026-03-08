import Foundation
import Combine
import SwiftData

/**
 * iOS implementation of CanvasRepository using SyncRepository as the source of truth.
 */
public final class CanvasRepositoryImpl: CanvasRepository {
    private let syncRepository: SyncRepository
    private let jsonDecoder = JSONDecoder()
    private let jsonEncoder = JSONEncoder()

    public init(syncRepository: SyncRepository) {
        self.syncRepository = syncRepository
    }

    public func getFeed() -> AnyPublisher<[FeedItem], Never> {
        syncRepository.observeAllSyncItems()
            .map { [jsonDecoder] items in
                var feedItems: [FeedItem] = []
                for item in items {
                    switch item.itemType {
                    case "SCRIBBLE":
                        if item.status == .completed,
                           let data = item.jsonData?.data(using: .utf8),
                           let workout = try? jsonDecoder.decode(ParsedWorkout.self, from: data) {
                            feedItems.append(.confirmation(ConfirmationItem(
                                id: item.id,
                                timestamp: item.createdAt,
                                workout: workout,
                                scribbleId: item.id
                            )))
                        } else {
                            let mappedStatus: ScribbleStatus = switch item.status {
                            case .pending: .pending
                            case .processing: .processing
                            case .failed: .failed
                            case .completed: .completed
                            }
                            feedItems.append(.scribble(ScribbleItem(
                                id: item.id,
                                timestamp: item.createdAt,
                                rawText: item.rawText ?? "",
                                status: mappedStatus
                            )))
                        }
                    case "PROMPT":
                        if let data = item.jsonData?.data(using: .utf8),
                           let dto = try? jsonDecoder.decode(PromptItem.self, from: data) {
                            feedItems.append(.prompt(dto))
                        }
                    case "CONFIRMATION":
                        if let data = item.jsonData?.data(using: .utf8),
                           let dto = try? jsonDecoder.decode(ConfirmationItem.self, from: data) {
                            feedItems.append(.confirmation(dto))
                        }
                    case "INSIGHT":
                        if let data = item.jsonData?.data(using: .utf8),
                           let dto = try? jsonDecoder.decode(InsightItem.self, from: data) {
                            feedItems.append(.insight(dto))
                        }
                    default: break
                    }
                }
                return feedItems.sorted { $0.feedTimestamp < $1.feedTimestamp }
            }
            .eraseToAnyPublisher()
    }

    public func addScribble(rawText: String) async throws {
        try await syncRepository.enqueueScribble(id: UUID().uuidString, rawText: rawText)
    }

    public func retryScribble(id: String) async throws {
        try await syncRepository.updateSyncStatus(id: id, status: .pending)
    }

    public func addConfirmation(item: ConfirmationItem) async throws {
        let data = try jsonEncoder.encode(item)
        if let jsonString = String(data: data, encoding: .utf8) {
            try await syncRepository.saveFeedItem(id: item.id, itemType: "CONFIRMATION", jsonData: jsonString, status: .completed)
        }
    }

    public func addInsight(item: InsightItem) async throws {
        let data = try jsonEncoder.encode(item)
        if let jsonString = String(data: data, encoding: .utf8) {
            try await syncRepository.saveFeedItem(id: item.id, itemType: "INSIGHT", jsonData: jsonString, status: .completed)
        }
    }

    public func removeFeedItem(id: String) async throws {
        try await syncRepository.deleteSyncItem(id: id)
    }

    public func clearFeed() async throws {}
}

private extension FeedItem {
    var feedTimestamp: Date {
        switch self {
        case .prompt(let item): return item.timestamp
        case .scribble(let item): return item.timestamp
        case .confirmation(let item): return item.timestamp
        case .insight(let item): return item.timestamp
        }
    }
}
