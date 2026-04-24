import Foundation
import SwiftData
import CoreModel
import CoreDatabase
import CoreCommon

@MainActor
public final class SettingsRepositoryImpl: SettingsRepository {
    private let modelContainer: ModelContainer
    private let modelContext: ModelContext

    public init(modelContainer: ModelContainer) {
        self.modelContainer = modelContainer
        self.modelContext = ModelContext(modelContainer)
    }

    public func exportUserData() async throws -> URL {
        // Fetch all scribbles
        let scribblesDescriptor = FetchDescriptor<ScribbleEntity>()
        
        let scribbles = try modelContext.fetch(scribblesDescriptor).map { $0.toDomain() }
        
        let exportData = UserExportData(
            scribbles: scribbles,
            exportDate: Date()
        )
        
        let encoder = JSONEncoder()
        encoder.outputFormatting = .prettyPrinted
        encoder.dateEncodingStrategy = .iso8601
        
        let data = try encoder.encode(exportData)
        
        let tempDir = FileManager.default.temporaryDirectory
        let fileURL = tempDir.appendingPathComponent("ScribbleFit_Export_\\(Date().timeIntervalSince1970).json")
        
        try data.write(to: fileURL)
        return fileURL
    }

    public func clearAllData() async throws {
        // Delete all entities
        try modelContext.delete(model: ScribbleEntity.self)
        try modelContext.delete(model: ExerciseEntity.self)
        try modelContext.delete(model: SetEntity.self)
        
        try modelContext.save()
    }
}

// Data structure for export
private struct UserExportData: Codable {
    let scribbles: [Scribble]
    let exportDate: Date
}
