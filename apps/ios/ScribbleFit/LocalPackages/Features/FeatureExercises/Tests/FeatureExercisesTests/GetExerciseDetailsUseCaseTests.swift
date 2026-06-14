import XCTest
import CoreModel
@testable import FeatureExercises

@MainActor
final class MockScribbleRepositoryDetails: ScribbleRepository {
    var scribblesToReturn: [Scribble] = []
    
    func observeScribbles(for date: Date) -> AsyncStream<[Scribble]> { AsyncStream { $0.finish() } }
    func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> { AsyncStream { $0.finish() } }
    func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]> {
        let streamData = scribblesToReturn.filter {
            $0.exercises.contains(where: { $0.canonicalName.lowercased() == exerciseName.lowercased() })
        }
        return AsyncStream { cont in 
            cont.yield(streamData)
            cont.finish() 
        }
    }
    func addScribble(_ scribble: Scribble) async throws {}
    func updateScribble(_ scribble: Scribble) async throws {}
    func deleteScribble(id: UUID) async throws {}
    func getScribble(id: UUID) async throws -> Scribble? { nil }
    func clearScribbleExercises(scribbleId: UUID) async throws {}
    func confirmScribble(_ scribble: Scribble) async throws {}
}

@MainActor
final class GetExerciseDetailsUseCaseTests: XCTestCase {
    private var mockRepo: MockScribbleRepositoryDetails!
    private var sut: GetExerciseDetailsUseCase!
    
    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockScribbleRepositoryDetails()
        sut = GetExerciseDetailsUseCase(scribbleRepository: mockRepo)
    }
    
    func test_execute_withEmptyHistory() async throws {
        mockRepo.scribblesToReturn = []
        let stream = sut.execute(exerciseName: "Bench Press")
        var results: [ExerciseDetails] = []
        for await detail in stream {
            results.append(detail)
        }
        XCTAssertEqual(results.count, 1)
        XCTAssertTrue(results[0].history.isEmpty)
        XCTAssertEqual(results[0].exerciseName, "Bench Press")
    }
    
    func test_execute_withHistory_returnsCorrectDetails() async throws {
        let exerciseId = UUID()
        let scribbleId = UUID()
        let exerciseSet = ExerciseSet(id: UUID(), setNumber: 1, weight: 100, reps: 5)
        let exercise = Exercise(id: exerciseId, scribbleId: scribbleId, canonicalName: "Squat", muscleGroup: "Legs", sets: [exerciseSet], createdAt: Date())
        let scribble = Scribble(id: scribbleId, rawText: "Squat 100x5", status: .completed, exercises: [exercise])
        
        mockRepo.scribblesToReturn = [scribble]
        
        let stream = sut.execute(exerciseName: "Squat")
        var results: [ExerciseDetails] = []
        for await detail in stream {
            results.append(detail)
        }
        
        XCTAssertEqual(results.count, 1)
        XCTAssertEqual(results[0].history.count, 1)
        XCTAssertEqual(results[0].muscleGroup, "Legs")
        XCTAssertEqual(results[0].weeklyStats.sessionsThisWeek, 1)
        XCTAssertEqual(results[0].history[0].maxWeight, 100)
    }
}
