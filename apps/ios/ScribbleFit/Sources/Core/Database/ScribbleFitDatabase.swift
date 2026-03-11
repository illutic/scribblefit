import Foundation
import SwiftData

@MainActor
public final class ScribbleFitDatabase {
    public static let shared = ScribbleFitDatabase(container: DatabaseContainer.shared.container)

    private let context: ModelContext

    public init(container: ModelContainer) {
        self.context = container.mainContext
    }

    // MARK: - ScribbleEntity

    public func insertSyncItem(_ item: ScribbleEntity) {
        context.insert(item)
        try? context.save()
    }

    public func getAllSyncItems() -> [ScribbleEntity] {
        let descriptor = FetchDescriptor<ScribbleEntity>(sortBy: [SortDescriptor(\.createdAt)])
        return (try? context.fetch(descriptor)) ?? []
    }

    public func updateSyncStatus(id: String, status: SyncStatus) {
        let descriptor = FetchDescriptor<ScribbleEntity>(predicate: #Predicate { $0.id == id })
        if let item = try? context.fetch(descriptor).first {
            item.syncStatus = status
            try? context.save()
        }
    }

    public func updateParsedResult(id: String, status: SyncStatus, jsonData: String) {
        let descriptor = FetchDescriptor<ScribbleEntity>(predicate: #Predicate { $0.id == id })
        if let item = try? context.fetch(descriptor).first {
            item.syncStatus = status
            item.jsonData = jsonData
            try? context.save()
        }
    }

    public func deleteSyncItem(id: String) {
        let descriptor = FetchDescriptor<ScribbleEntity>(predicate: #Predicate { $0.id == id })
        if let item = try? context.fetch(descriptor).first {
            context.delete(item)
            try? context.save()
        }
    }

    // MARK: - WorkoutEntity

    public func getAllWorkoutLogs() -> [WorkoutEntity] {
        let descriptor = FetchDescriptor<WorkoutEntity>(sortBy: [SortDescriptor(\.date, order: .reverse)])
        return (try? context.fetch(descriptor)) ?? []
    }

    public func upsertWorkoutLog(_ log: WorkoutEntity) {
        let id = log.id
        let descriptor = FetchDescriptor<WorkoutEntity>(predicate: #Predicate { $0.id == id })
        if let existing = try? context.fetch(descriptor).first {
            existing.date = log.date
            existing.location = log.location
            existing.totalVolume = log.totalVolume
        } else {
            context.insert(log)
        }
        try? context.save()
    }

    // MARK: - WorkoutSet

    public func getSetsForWorkout(id workoutId: String) -> [WorkoutSet] {
        let descriptor = FetchDescriptor<WorkoutSet>(predicate: #Predicate { $0.exerciseId == workoutId })
        return (try? context.fetch(descriptor)) ?? []
    }

    public func upsertWorkoutSets(_ sets: [WorkoutSet]) {
        for set in sets {
            context.insert(set)
        }
        try? context.save()
    }

    // MARK: - ExerciseEntity

    public func insertExercisesIfAbsent(_ exercises: [ExerciseEntity]) {
        for exercise in exercises {
            let id = exercise.id
            let descriptor = FetchDescriptor<ExerciseEntity>(predicate: #Predicate { $0.id == id })
            if (try? context.fetch(descriptor).first) == nil {
                context.insert(exercise)
            }
        }
        try? context.save()
    }

    // MARK: - SystemConfigEntity

    public func getConfig() -> SystemConfigEntity? {
        let descriptor = FetchDescriptor<SystemConfigEntity>(predicate: #Predicate { $0.id == "config" })
        return try? context.fetch(descriptor).first
    }

    public func upsertConfig(_ config: SystemConfigEntity) {
        let descriptor = FetchDescriptor<SystemConfigEntity>(predicate: #Predicate { $0.id == "config" })
        if let existing = try? context.fetch(descriptor).first {
            existing.promptVersion = config.promptVersion
            existing.promptText = config.promptText
            existing.exerciseVersion = config.exerciseVersion
            existing.preferredLlmProvider = config.preferredLlmProvider
            existing.preferredModel = config.preferredModel
            existing.parsingMode = config.parsingMode
            existing.weightUnit = config.weightUnit
            existing.themePreference = config.themePreference
            existing.updatedAt = config.updatedAt
        } else {
            context.insert(config)
        }
        try? context.save()
    }

    // MARK: - InsightsCache

    public func getInsightByKey(key: String) -> InsightsCache? {
        let descriptor = FetchDescriptor<InsightsCache>(predicate: #Predicate { $0.key == key })
        return try? context.fetch(descriptor).first
    }

    public func upsertInsight(_ cache: InsightsCache) {
        let key = cache.key
        let descriptor = FetchDescriptor<InsightsCache>(predicate: #Predicate { $0.key == key })
        if let existing = try? context.fetch(descriptor).first {
            existing.jsonData = cache.jsonData
            existing.createdAt = cache.createdAt
        } else {
            context.insert(cache)
        }
        try? context.save()
    }

    public func clearInsights() {
        let descriptor = FetchDescriptor<InsightsCache>()
        if let all = try? context.fetch(descriptor) {
            all.forEach { context.delete($0) }
        }
        try? context.save()
    }

    // MARK: - ActiveSession

    public func getActiveSession() -> ActiveSession? {
        let descriptor = FetchDescriptor<ActiveSession>()
        return try? context.fetch(descriptor).first
    }

    public func upsertActiveSession(_ session: ActiveSession) {
        let descriptor = FetchDescriptor<ActiveSession>()
        if let existing = try? context.fetch(descriptor).first {
            existing.jsonData = session.jsonData
            existing.updatedAt = session.updatedAt
        } else {
            context.insert(session)
        }
        try? context.save()
    }

    public func clearActiveSession() {
        let descriptor = FetchDescriptor<ActiveSession>()
        if let all = try? context.fetch(descriptor) {
            all.forEach { context.delete($0) }
        }
        try? context.save()
    }

    // MARK: - Nuclear

    public func deleteAll() {
        try? context.delete(model: ScribbleEntity.self)
        try? context.delete(model: WorkoutEntity.self)
        try? context.delete(model: ExerciseEntity.self)
        try? context.delete(model: SystemConfigEntity.self)
        try? context.delete(model: InsightsCache.self)
        try? context.delete(model: ActiveSession.self)
        try? context.save()
    }
}
