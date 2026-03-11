import Foundation
import SwiftData

public final class DatabaseContainer: @unchecked Sendable {
    @MainActor public static let shared = DatabaseContainer()

    public let container: ModelContainer

    private init() {
        let schema = Schema([
            WorkoutEntity.self,
            WorkoutSet.self,
            ScribbleEntity.self,
            ExerciseEntity.self,
            SystemConfigEntity.self,
            InsightsCache.self,
            ActiveSession.self
        ])
        let config = ModelConfiguration(schema: schema, isStoredInMemoryOnly: false)
        do {
            container = try ModelContainer(for: schema, configurations: [config])
        } catch {
            fatalError("Failed to create ModelContainer: \(error)")
        }
    }
}
