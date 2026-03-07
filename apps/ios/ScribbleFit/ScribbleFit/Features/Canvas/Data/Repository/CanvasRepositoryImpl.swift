import Foundation
import SwiftData

/**
 * iOS implementation of CanvasRepository using SwiftData.
 */
public final class CanvasRepositoryImpl: CanvasRepository {
    private let database: ScribbleFitDatabase
    private let jsonDecoder = JSONDecoder()
    private let jsonEncoder = JSONEncoder()
    
    public init(database: ScribbleFitDatabase = .shared) {
        self.database = database
    }
    
    public func getFeed() async throws -> [FeedItem] {
        var entities = database.getCanvasFeed()
        
        if entities.isEmpty {
            // Seed with an initial prompt if empty
            let id = UUID().uuidString
            let now = Date()
            let prompt = PromptItem(id: id, timestamp: now, text: "Ready for a Push day?", emoji: "💪", type: .pattern)
            let data = try jsonEncoder.encode(prompt)
            if let jsonString = String(data: data, encoding: .utf8) {
                let entity = CanvasFeed(id: id, itemType: "PROMPT", jsonData: jsonString, createdAt: now)
                database.upsertCanvasFeedItem(entity)
                // Manually add to entities list to avoid immediate re-fetch loop
                entities = [entity]
            }
        }
        
        return entities.compactMap { entity in
            guard let data = entity.jsonData.data(using: .utf8) else { return nil }
            
            switch entity.itemType {
            case "SCRIBBLE":
                let dto = try? jsonDecoder.decode(ScribbleItem.self, from: data)
                return dto.map { .scribble($0) }
            case "PROMPT":
                let dto = try? jsonDecoder.decode(PromptItem.self, from: data)
                return dto.map { .prompt($0) }
            case "CONFIRMATION":
                let dto = try? jsonDecoder.decode(ConfirmationItem.self, from: data)
                return dto.map { .confirmation($0) }
            case "INSIGHT":
                let dto = try? jsonDecoder.decode(InsightItem.self, from: data)
                return dto.map { .insight($0) }
            default: return nil
            }
        }
    }
    
    public func addScribble(rawText: String) async throws {
        let id = UUID().uuidString
        let now = Date()
        
        // 1. Persist to Sync Queue
        let syncItem = SyncQueue(id: id, rawText: rawText, status: .pending, createdAt: now)
        database.upsertSyncItem(syncItem)
        
        // 2. Add to Canvas Feed
        let scribble = ScribbleItem(id: id, timestamp: now, rawText: rawText, status: .pending)
        let data = try jsonEncoder.encode(scribble)
        if let jsonString = String(data: data, encoding: .utf8) {
            let entity = CanvasFeed(id: id, itemType: "SCRIBBLE", jsonData: jsonString, createdAt: now)
            database.upsertCanvasFeedItem(entity)
        }
    }
    
    public func retryScribble(id: String) async throws {
        database.updateSyncStatus(id: id, status: .pending)
    }
    
    public func addConfirmation(item: ConfirmationItem) async throws {
        let data = try jsonEncoder.encode(item)
        if let jsonString = String(data: data, encoding: .utf8) {
            let entity = CanvasFeed(id: item.id, itemType: "CONFIRMATION", jsonData: jsonString, createdAt: item.timestamp)
            database.upsertCanvasFeedItem(entity)
        }
    }
    
    public func clearFeed() async throws {
        database.clearCanvasFeed()
    }
}
