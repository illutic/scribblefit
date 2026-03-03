import Foundation
import SwiftData

@MainActor
public final class ScribbleFitDatabase {
    public static let shared = ScribbleFitDatabase(context: DatabaseContainer.shared.mainContext)
    
    private let context: ModelContext
    
    public init(context: ModelContext) {
        self.context = context
    }
    
    // MARK: - WorkoutLog
    
    public func upsertWorkoutLog(_ log: WorkoutLog) {
        context.insert(log)
        try? context.save()
    }
    
    public func getAllWorkoutLogs() -> [WorkoutLog] {
        let descriptor = FetchDescriptor<WorkoutLog>(sortBy: [SortDescriptor(\.date, order: .reverse)])
        return (try? context.fetch(descriptor)) ?? []
    }
    
    public func getWorkoutLog(id: String) -> WorkoutLog? {
        let descriptor = FetchDescriptor<WorkoutLog>(predicate: #Predicate { $0.id == id })
        return try? context.fetch(descriptor).first
    }
    
    public func deleteWorkoutLog(_ log: WorkoutLog) {
        context.delete(log)
        try? context.save()
    }
    
    // MARK: - WorkoutSet
    
    public func upsertWorkoutSet(_ set: WorkoutSet) {
        context.insert(set)
        try? context.save()
    }
    
    public func getSetsForWorkout(id: String) -> [WorkoutSet] {
        let descriptor = FetchDescriptor<WorkoutSet>(predicate: #Predicate { $0.workout?.id == id })
        return (try? context.fetch(descriptor)) ?? []
    }
    
    public func deleteWorkoutSet(_ set: WorkoutSet) {
        context.delete(set)
        try? context.save()
    }
    
    // MARK: - ExerciseDictionary
    
    public func upsertExercises(_ exercises: [ExerciseDictionary]) {
        for exercise in exercises {
            context.insert(exercise)
        }
        try? context.save()
    }
    
    public func getAllExercises() -> [ExerciseDictionary] {
        let descriptor = FetchDescriptor<ExerciseDictionary>(sortBy: [SortDescriptor(\.canonicalName)])
        return (try? context.fetch(descriptor)) ?? []
    }
    
    public func searchExercises(query: String) -> [ExerciseDictionary] {
        // SwiftData Predicate is a bit limited for complex LIKE queries
        // For now, we fetch and filter if necessary, or use basic contains
        let descriptor = FetchDescriptor<ExerciseDictionary>(
            predicate: #Predicate { 
                $0.canonicalName.contains(query)
            }
        )
        return (try? context.fetch(descriptor)) ?? []
    }
    
    // MARK: - SyncQueue
    
    public func upsertSyncItem(_ item: SyncQueue) {
        context.insert(item)
        try? context.save()
    }
    
    public func getSyncItems(status: SyncStatus) -> [SyncQueue] {
        let statusRaw = status.rawValue
        let descriptor = FetchDescriptor<SyncQueue>(
            predicate: #Predicate { $0.status == statusRaw },
            sortBy: [SortDescriptor(\.createdAt)]
        )
        return (try? context.fetch(descriptor)) ?? []
    }
    
    public func updateSyncStatus(id: String, status: SyncStatus) {
        if let item = (try? context.fetch(FetchDescriptor<SyncQueue>(predicate: #Predicate { $0.id == id })) )?.first {
            item.syncStatus = status
            try? context.save()
        }
    }
    
    // MARK: - SystemConfig
    
    public func upsertConfig(_ config: SystemConfig) {
        context.insert(config)
        try? context.save()
    }
    
    public func getConfig(id: String = "config") -> SystemConfig? {
        let descriptor = FetchDescriptor<SystemConfig>(predicate: #Predicate { $0.id == id })
        return try? context.fetch(descriptor).first
    }
    
    // MARK: - InsightsCache
    
    public func upsertInsight(_ insight: InsightsCache) {
        context.insert(insight)
        try? context.save()
    }
    
    public func getInsight(key: String) -> InsightsCache? {
        let descriptor = FetchDescriptor<InsightsCache>(predicate: #Predicate { $0.key == key })
        return try? context.fetch(descriptor).first
    }
    
    // MARK: - General
    
    public func deleteAll() {
        try? context.delete(model: SyncQueue.self)
        try? context.delete(model: WorkoutLog.self)
        try? context.delete(model: WorkoutSet.self)
        try? context.delete(model: ExerciseDictionary.self)
        try? context.delete(model: SystemConfig.self)
        try? context.delete(model: InsightsCache.self)
        try? context.save()
    }
}
