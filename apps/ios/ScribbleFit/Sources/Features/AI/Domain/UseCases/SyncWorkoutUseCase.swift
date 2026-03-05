import Foundation

public final class SyncWorkoutUseCase: Sendable {
    private let syncRepository: SyncRepository
    private let telemetryRepository: TelemetryRepository
    private let secureKeyStorage: SecureKeyStorage
    private let openAIEngine: LLMEngine
    private let proxyEngine: LLMEngine
    private let promptVersion: String
    
    public init(
        syncRepository: SyncRepository,
        telemetryRepository: TelemetryRepository,
        secureKeyStorage: SecureKeyStorage,
        openAIEngine: LLMEngine,
        proxyEngine: LLMEngine,
        promptVersion: String
    ) {
        self.syncRepository = syncRepository
        self.telemetryRepository = telemetryRepository
        self.secureKeyStorage = secureKeyStorage
        self.openAIEngine = openAIEngine
        self.proxyEngine = proxyEngine
        self.promptVersion = promptVersion
    }
    
    public func execute() async throws {
        let pendingItems = try await syncRepository.getPendingSyncItems()
        
        for item in pendingItems {
            try await syncRepository.updateSyncStatus(id: item.id, status: .processing)
            
            // Priority 1: Personal API Key (BYOK)
            let apiKey = try await secureKeyStorage.getApiKey()
            let engine = (apiKey != nil) ? openAIEngine : proxyEngine
            
            do {
                let workout = try await engine.parseWorkout(rawText: item.rawText)
                try await syncRepository.saveParsedWorkout(syncItemId: item.id, workout: workout)
            } catch {
                try await syncRepository.updateSyncStatus(id: item.id, status: .failed)
                
                let errorMessage: String = if let parsingError = error as? AIParsingError {
                    parsingError.error
                } else {
                    error.localizedDescription
                }
                
                // Report to telemetry
                let telemetryData = TelemetryData(
                    rawText: item.rawText,
                    promptVersion: promptVersion,
                    errorMessage: errorMessage
                )
                try? await telemetryRepository.reportError(data: telemetryData)
            }
        }
    }
}
