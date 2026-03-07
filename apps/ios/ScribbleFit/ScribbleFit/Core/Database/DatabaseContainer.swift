import Foundation
import SwiftData

@MainActor
public final class DatabaseContainer {
    public static let shared = DatabaseContainer()
    
    public let container: ModelContainer
    
    private init() {
        let schema = Schema([
            SyncQueue.self,
            WorkoutLog.self,
            WorkoutSet.self,
            ExerciseDictionary.self,
            SystemConfig.self,
            InsightsCache.self,
            CanvasFeed.self,
            ActiveSession.self
        ])
        let modelConfiguration = ModelConfiguration(schema: schema, isStoredInMemoryOnly: false)
        
        do {
            container = try ModelContainer(for: schema, configurations: [modelConfiguration])
        } catch {
            fatalError("Could not create ModelContainer: \(error)")
        }
    }
    
    public var mainContext: ModelContext {
        container.mainContext
    }
}
