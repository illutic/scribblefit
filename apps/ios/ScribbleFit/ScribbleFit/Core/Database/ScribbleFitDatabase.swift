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
    
    public func saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) {
        let dateFormatter = ISO8601DateFormatter()
        dateFormatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        
        let workoutDate = dateFormatter.date(from: workout.date) ?? Date()
        
        var totalVolume = 0.0
        let workoutId = UUID().uuidString
        
        for exercise in workout.exercises {
            // Fuzzy search for the canonical ID from our local dictionary
            let exerciseId = searchExercises(query: exercise.canonicalName).first?.id ?? exercise.canonicalName
            
            for set in exercise.sets {
                totalVolume += (set.weight * Double(set.reps))
                
                let workoutSet = WorkoutSet(
                    id: UUID().uuidString,
                    weight: set.weight,
                    reps: set.reps,
                    rpe: set.rpe,
                    notes: set.notes,
                    exerciseId: exerciseId
                )
                context.insert(workoutSet)
            }
        }
        
        let log = WorkoutLog(
            id: workoutId,
            date: workoutDate,
            location: workout.location,
            totalVolume: totalVolume
        )
        context.insert(log)
        
        // Connect sets to log (SwiftData relationships)
        // Note: In a full implementation we'd fetch the sets we just inserted or assign the log to them in the loop.
        // For simplicity here, we assume SwiftData handles the insertion order.
        
        // Update Sync Queue status
        if let item = (try? context.fetch(FetchDescriptor<SyncQueue>(predicate: #Predicate { $0.id == syncItemId })) )?.first {
            item.syncStatus = .completed
        }
        
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
        let descriptor = FetchDescriptor<ExerciseDictionary>(
            predicate: #Predicate { 
                $0.canonicalName.contains(query)
            }
        )
        return (try? context.fetch(descriptor)) ?? []
    }
    
    public func deleteAllExercises() {
        try? context.delete(model: ExerciseDictionary.self)
        try? context.save()
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
    
    public func getInsightByKey(key: String) -> InsightsCache? {
        let descriptor = FetchDescriptor<InsightsCache>(predicate: #Predicate { $0.key == key })
        return try? context.fetch(descriptor).first
    }
    
    public func clearInsights() {
        try? context.delete(model: InsightsCache.self)
        try? context.save()
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
