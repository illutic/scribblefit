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
        return database.getCanvasFeed().compactMap { entity in
            guard let data = entity.jsonData.data(using: .utf8) else { return nil }
            
            switch entity.itemType {
            case "SCRIBBLE":
                let dto = try? jsonDecoder.decode(ScribbleItem.self, from: data)
                return dto.map { .scribble($0) }
            case "PROMPT":
                let dto = try? jsonDecoder.decode(PromptItem.self, from: data)
                return dto.map { .prompt($0) }
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
        // Implementation for confirmation cards
    }
    
    public func clearFeed() async throws {
        database.clearCanvasFeed()
    }
}
