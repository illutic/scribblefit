import Foundation
import SwiftData

/**
 * iOS implementation of SyncRepository using ScribbleFitDatabase (SwiftData).
 */
@MainActor
public final class SyncRepositoryImpl: SyncRepository {
    private let database: ScribbleFitDatabase
    private var syncWorkoutUseCase: SyncWorkoutUseCase?
    
    public init(database: ScribbleFitDatabase = .shared) {
        self.database = database
    }
    
    // Lazy injection to avoid circular dependency if any
    public func setSyncWorkoutUseCase(_ useCase: SyncWorkoutUseCase) {
        self.syncWorkoutUseCase = useCase
    }
    
    public func getPendingSyncItems() async throws -> [AISyncItem] {
        return database.getSyncItems(status: .pending).map { $0.toDomain() }
    }
    
    public func getAllSyncItems() async throws -> [AISyncItem] {
        return database.getAllSyncItems().map { $0.toDomain() }
    }
    
    public func updateSyncStatus(id: String, status: AISyncStatus) async throws {
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
    
    public func enqueueScribble(id: String, rawText: String) async throws {
        let syncItem = SyncQueue(
            id: id,
            itemType: "SCRIBBLE",
            rawText: rawText,
            status: .pending,
            createdAt: Date()
        )
        database.upsertSyncItem(syncItem)
        triggerImmediateSync()
    }
    
    public func saveFeedItem(id: String, itemType: String, jsonData: String, status: AISyncStatus) async throws {
        let dbStatus: ScribbleFit.SyncStatus = switch status {
        case .pending: .pending
        case .processing: .processing
        case .completed: .completed
        case .failed: .failed
        }
        
        let syncItem = SyncQueue(
            id: id,
            itemType: itemType,
            status: dbStatus,
            jsonData: jsonData,
            createdAt: Date()
        )
        database.upsertSyncItem(syncItem)
    }
    
    public func deleteSyncItem(id: String) async throws {
        database.deleteSyncItem(id: id)
    }
    
    private func triggerImmediateSync() {
        Task {
            do {
                try await syncWorkoutUseCase?.execute()
            } catch {
                print("Error executing sync: \(error)")
            }
        }
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
            itemType: self.itemType,
            rawText: self.rawText,
            status: domainStatus,
            jsonData: self.jsonData,
            createdAt: self.createdAt
        )
    }
}
