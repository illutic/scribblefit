import Foundation
import SwiftData
import Security

@MainActor
public final class SettingsRepositoryImpl: SettingsRepository {
    private let modelContainer: ModelContainer
    private let modelContext: ModelContext
    private let keychain = KeychainHelper.shared
    private let service = "com.scribblefit.ai"
    private let apiKeyAccount = "llm_api_key"

    public init(modelContainer: ModelContainer) {
        self.modelContainer = modelContainer
        self.modelContext = ModelContext(modelContainer)
    }

    public func getApiKey() async throws -> String? {
        return keychain.readString(service: service, account: apiKeyAccount)
    }

    public func saveApiKey(_ apiKey: String) async throws {
        keychain.saveString(apiKey, service: service, account: apiKeyAccount)
    }

    public func clearApiKey() async throws {
        keychain.delete(service: service, account: apiKeyAccount)
    }

    public func exportUserData() async throws -> URL {
        // Fetch all scribbles and workouts
        let scribblesDescriptor = FetchDescriptor<ScribbleEntity>()
        let workoutsDescriptor = FetchDescriptor<WorkoutEntity>()
        
        let scribbles = try modelContext.fetch(scribblesDescriptor).map { $0.toDomain() }
        let workouts = try modelContext.fetch(workoutsDescriptor).map { $0.toDomain() }
        
        let exportData = UserExportData(
            scribbles: scribbles,
            workouts: workouts,
            exportDate: Date()
        )
        
        let encoder = JSONEncoder()
        encoder.outputFormatting = .prettyPrinted
        encoder.dateEncodingStrategy = .iso8601
        
        let data = try encoder.encode(exportData)
        
        let tempDir = FileManager.default.temporaryDirectory
        let fileURL = tempDir.appendingPathComponent("ScribbleFit_Export_\(Date().timeIntervalSince1970).json")
        
        try data.write(to: fileURL)
        return fileURL
    }

    public func clearAllData() async throws {
        // Delete all entities
        try modelContext.delete(model: ScribbleEntity.self)
        try modelContext.delete(model: WorkoutEntity.self)
        try modelContext.delete(model: ExerciseEntity.self)
        try modelContext.delete(model: SetEntity.self)
        
        try modelContext.save()
        
        // Also clear API key
        try await clearApiKey()
    }
}

// Data structure for export
private struct UserExportData: Codable {
    let scribbles: [Scribble]
    let workouts: [Workout]
    let exportDate: Date
}
