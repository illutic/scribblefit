import Foundation
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
    
    public func getFeed() async throws -> [FeedItem] {
        let items = try await syncRepository.getAllSyncItems()
        
        if items.isEmpty {
            let id = UUID().uuidString
            let now = Date()
            let prompt = PromptItem(id: id, timestamp: now, text: "Ready for a Push day?", emoji: "💪", type: .pattern)
            let data = try jsonEncoder.encode(prompt)
            if let jsonString = String(data: data, encoding: .utf8) {
                try await syncRepository.saveFeedItem(id: id, itemType: "PROMPT", jsonData: jsonString, status: .completed)
            }
            return try await getFeed() // Re-fetch after initial prompt
        }
        
        var feedItems: [FeedItem] = []
        
        for item in items {
            switch item.itemType {
            case "SCRIBBLE":
                if item.status == .completed {
                    // In a real app we'd fetch the ParsedWorkout from a separate table or property
                    // For now we map to confirmation if completed
                    // Since iOS SyncItem doesn't have ParsedWorkout directly in this simplified model yet
                    // let's assume it's handled.
                    feedItems.append(.scribble(ScribbleItem(
                        id: item.id,
                        timestamp: item.createdAt,
                        rawText: item.rawText ?? "",
                        status: .completed
                    )))
                } else {
                    feedItems.append(.scribble(ScribbleItem(
                        id: item.id,
                        timestamp: item.createdAt,
                        rawText: item.rawText ?? "",
                        status: switch item.status {
                        case .pending: .pending
                        case .processing: .processing
                        case .failed: .failed
                        case .completed: .completed
                        }
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
        
        return feedItems.sorted { $0.timestamp < $1.timestamp }
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
    
    public func clearFeed() async throws {
        // Implementation for clearing specific items if needed
    }
}

private extension FeedItem {
    var timestamp: Date {
        switch self {
        case .prompt(let item): return item.timestamp
        case .scribble(let item): return item.timestamp
        case .confirmation(let item): return item.timestamp
        case .insight(let item): return item.timestamp
        }
    }
}
