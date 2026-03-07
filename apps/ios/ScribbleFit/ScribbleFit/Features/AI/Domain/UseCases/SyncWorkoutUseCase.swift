import Foundation

@MainActor
public final class SyncWorkoutUseCase {
    private let syncRepository: SyncRepository
    private let telemetryRepository: TelemetryRepository
    private let engine: LLMEngine
    private let configRepository: ConfigRepository
    
    public init(
        syncRepository: SyncRepository,
        telemetryRepository: TelemetryRepository,
        engine: LLMEngine,
        configRepository: ConfigRepository
    ) {
        self.syncRepository = syncRepository
        self.telemetryRepository = telemetryRepository
        self.engine = engine
        self.configRepository = configRepository
    }
    
    public func execute() async throws {
        let config = await configRepository.getConfig()
        let promptVersion = config?.promptVersion ?? "1.0.0"
        
        let pendingItems = try await syncRepository.getPendingSyncItems()
        
        for item in pendingItems {
            try await syncRepository.updateSyncStatus(id: item.id, status: .processing)
            
            let result = await engine.parseWorkout(rawText: item.rawText)
            
            if result.status == .success, let workout = result.workout {
                try await syncRepository.saveParsedWorkout(syncItemId: item.id, workout: workout)
            } else {
                try await syncRepository.updateSyncStatus(id: item.id, status: .failed)
                
                // Report to telemetry
                let telemetryData = TelemetryData(
                    rawText: item.rawText,
                    promptVersion: promptVersion,
                    errorMessage: result.error ?? "Unknown error during parsing"
                )
                try? await telemetryRepository.reportError(data: telemetryData)
            }
        }
    }
}
