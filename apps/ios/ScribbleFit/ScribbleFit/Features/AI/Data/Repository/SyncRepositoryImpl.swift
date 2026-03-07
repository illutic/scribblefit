import Foundation
import SwiftData
import BackgroundTasks

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
    
    public func enqueueScribble(rawText: String) async throws {
        let syncItem = SyncQueue(
            id: UUID().uuidString,
            rawText: rawText,
            status: .pending,
            createdAt: Date()
        )
        database.upsertSyncItem(syncItem)
        triggerImmediateSync()
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
            rawText: self.rawText,
            status: domainStatus,
            createdAt: self.createdAt
        )
    }
}
