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
        let log = WorkoutLog(id: "1", location: "Gym")
        database.upsertWorkoutLog(log)
        
        let fetched = database.getWorkoutLog(id: "1")
        XCTAssertNotNil(fetched)
        XCTAssertEqual(fetched?.location, "Gym")
    }

    @MainActor
    func testGetAllWorkoutLogsSorted() {
        let log1 = WorkoutLog(id: "1", date: Date().addingTimeInterval(-100))
        let log2 = WorkoutLog(id: "2", date: Date())
        
        database.upsertWorkoutLog(log1)
        database.upsertWorkoutLog(log2)
        
        let all = database.getAllWorkoutLogs()
        XCTAssertEqual(all.count, 2)
        XCTAssertEqual(all.first?.id, "2") // Most recent first
    }

    @MainActor
    func testUpsertAndGetSetsForWorkout() {
        let log = WorkoutLog(id: "w1")
        database.upsertWorkoutLog(log)
        
        let set1 = WorkoutSet(id: "s1", weight: 100, reps: 5, exerciseId: "ex1")
        set1.workout = log
        database.upsertWorkoutSet(set1)
        
        let sets = database.getSetsForWorkout(id: "w1")
        XCTAssertEqual(sets.count, 1)
        XCTAssertEqual(sets.first?.id, "s1")
    }
    
    @MainActor
    func testDeleteWorkoutLogCascadesToSets() {
        let log = WorkoutLog(id: "w1")
        database.upsertWorkoutLog(log)
        
        let set1 = WorkoutSet(id: "s1", weight: 100, reps: 5, exerciseId: "ex1")
        set1.workout = log
        database.upsertWorkoutSet(set1)
        
        database.deleteWorkoutLog(log)
        
        let fetchedLog = database.getWorkoutLog(id: "w1")
        XCTAssertNil(fetchedLog)
        
        let sets = database.getSetsForWorkout(id: "w1")
        XCTAssertEqual(sets.count, 0)
    }
}
