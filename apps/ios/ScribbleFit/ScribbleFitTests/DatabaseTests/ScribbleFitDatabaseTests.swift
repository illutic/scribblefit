import XCTest
import SwiftData
@testable import ScribbleFit

@MainActor
final class ScribbleFitDatabaseTests: XCTestCase {
    var database: ScribbleFitDatabase!
    var container: ModelContainer!
    var context: ModelContext!

    override func setUp() async throws {
        try await super.setUp()
        let schema = Schema([
            SyncQueue.self,
            WorkoutLog.self,
            WorkoutSet.self,
            ExerciseDictionary.self,
            SystemConfig.self,
            InsightsCache.self
        ])
        let config = ModelConfiguration(isStoredInMemoryOnly: true)
        let container = try ModelContainer(for: schema, configurations: [config])
        
        await MainActor.run {
            self.container = container
            self.context = container.mainContext
            self.database = ScribbleFitDatabase(context: self.context)
        }
    }

    override func tearDown() async throws {
        await MainActor.run {
            self.database = nil
            self.container = nil
            self.context = nil
        }
        try await super.tearDown()
    }

    @MainActor
    func testUpsertAndGetWorkoutLog() {
        let log = WorkoutLog(id: "1", date: Date(), location: "Gym", totalVolume: 1000)
        database.upsertWorkoutLog(log)
        
        let fetched = database.getWorkoutLog(id: "1")
        XCTAssertNotNil(fetched)
        XCTAssertEqual(fetched?.location, "Gym")
    }

    @MainActor
    func testGetAllWorkoutLogsSorted() {
        let log1 = WorkoutLog(id: "1", date: Date().addingTimeInterval(-100), location: "Gym", totalVolume: 1000)
        let log2 = WorkoutLog(id: "2", date: Date(), location: "Gym", totalVolume: 1000)
        
        database.upsertWorkoutLog(log1)
        database.upsertWorkoutLog(log2)
        
        let all = database.getAllWorkoutLogs()
        XCTAssertEqual(all.count, 2)
        XCTAssertEqual(all.first?.id, "2") // Most recent first
    }

    @MainActor
    func testSaveParsedWorkoutCalculatesVolumeAndMapsExercises() {
        // Seed exercise dictionary
        let exercise = ExerciseDictionary(id: "ex1", canonicalName: "Bench Press", muscleGroup: "Chest", aliases: ["bench"])
        database.upsertExercises([exercise])
        
        // Prepare parsed workout
        let workout = ParsedWorkout(
            date: ISO8601DateFormatter().string(from: Date()),
            location: "Gym",
            exercises: [
                ParsedExercise(
                    canonicalName: "Bench Press",
                    sets: [
                        ParsedSet(weight: 100, reps: 10),
                        ParsedSet(weight: 150, reps: 5)
                    ]
                )
            ]
        )
        
        database.saveParsedWorkout(syncItemId: "sync1", workout: workout)
        
        let logs = database.getAllWorkoutLogs()
        XCTAssertEqual(logs.count, 1)
        let log = logs.first!
        XCTAssertEqual(log.totalVolume, 1000 + 750)
        XCTAssertEqual(log.location, "Gym")
        
        let sets = database.getSetsForWorkout(id: log.id)
        XCTAssertEqual(sets.count, 2)
        XCTAssertEqual(sets.first?.exerciseId, "ex1")
    }

    @MainActor
    func testSystemConfigPersistence() {
        let config = SystemConfig(id: "config", promptVersion: "1.0.0", promptText: "Test Prompt", exerciseVersion: "1.0.0", updatedAt: Date())
        database.upsertConfig(config)
        
        let fetched = database.getConfig()
        XCTAssertNotNil(fetched)
        XCTAssertEqual(fetched?.promptVersion, "1.0.0")
        XCTAssertEqual(fetched?.exerciseVersion, "1.0.0")
    }
    
    @MainActor
    func testSyncQueueOperations() {
        let item = SyncQueue(id: "1", itemType: "SCRIBBLE", rawText: "bench 100x5", status: .pending, createdAt: Date())
        database.upsertSyncItem(item)
        
        var pending = database.getSyncItems(status: .pending)
        XCTAssertEqual(pending.count, 1)
        
        database.updateSyncStatus(id: "1", status: .completed)
        pending = database.getSyncItems(status: .pending)
        XCTAssertEqual(pending.count, 0)
        
        let completed = database.getSyncItems(status: .completed)
        XCTAssertEqual(completed.count, 1)
        XCTAssertEqual(completed.first?.itemType, "SCRIBBLE")
    }

    @MainActor
    func testGetAllSyncItems() {
        let item1 = SyncQueue(id: "1", itemType: "SCRIBBLE", rawText: "bench 100x5", status: .pending, createdAt: Date().addingTimeInterval(-10))
        let item2 = SyncQueue(id: "2", itemType: "PROMPT", status: .completed, jsonData: "{}", createdAt: Date())
        
        database.upsertSyncItem(item1)
        database.upsertSyncItem(item2)
        
        let all = database.getAllSyncItems()
        XCTAssertEqual(all.count, 2)
        XCTAssertEqual(all.first?.id, "1")
        XCTAssertEqual(all.last?.id, "2")
    }
}
