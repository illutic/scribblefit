import Foundation
import SwiftData

/**
 * iOS implementation of SyncRepository using ScribbleFitDatabase (SwiftData).
 */
@MainActor
public final class SyncRepositoryImpl: SyncRepository {
    private let database: ScribbleFitDatabase
    
    public init(database: ScribbleFitDatabase = .shared) {
        self.database = database
    }
    
    public func getPendingSyncItems() async throws -> [AISyncItem] {
        return database.getSyncItems(status: .pending).map { $0.toDomain() }
    }
    
    public func updateSyncStatus(id: String, status: AISyncStatus) async throws {
        // Map domain status to database status
        let dbStatus: ScribbleFit.SyncStatus = switch status {
        case .pending: .pending
        case .processing: .processing
        case .completed: .completed
        case .failed: .failed
        }
        database.updateSyncStatus(id: id, status: dbStatus)
    }
    
    public func saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) async throws {
        database.saveParsedWorkout(syncItemId: syncItemId, workout: workout)
    }
}

private extension SyncQueue {
    func toDomain() -> AISyncItem {
        let dbStatus = ScribbleFit.SyncStatus(rawValue: self.status) ?? .pending
        let domainStatus: AISyncStatus = switch dbStatus {
        case .pending: .pending
        case .processing: .processing
        case .completed: .completed
        case .failed: .failed
        }
        
        return AISyncItem(
            id: self.id,
            rawText: self.rawText,
            status: domainStatus,
            createdAt: self.createdAt
        )
    }
}
