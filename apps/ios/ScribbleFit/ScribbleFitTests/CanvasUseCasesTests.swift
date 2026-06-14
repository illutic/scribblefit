import XCTest
import CoreModel
@testable import ScribbleFit

@MainActor
final class MockCanvasRepository: CanvasRepository {
    var addedScribbles: [String] = []
    
    func addScribble(rawText: String) async throws {
        addedScribbles.append(rawText)
    }
}

@MainActor
final class MockWorkoutSessionRepository: WorkoutSessionRepository {
    var clearedSession = false
    
    func clearActiveSession() async throws {
        clearedSession = true
    }
}

@MainActor
final class MockLedgerRepository: LedgerRepository {
    var loggedWorkouts: [WorkoutHistory] = []
    
    func logWorkout(_ workout: WorkoutHistory) async throws {
        loggedWorkouts.append(workout)
    }
}

@MainActor
final class CanvasUseCasesTests: XCTestCase {
    
    // MARK: - ExecuteQuickActionUseCase
    func test_executeQuickAction_repeatLast() async throws {
        let repo = MockCanvasRepository()
        let sut = ExecuteQuickActionUseCase(canvasRepository: repo)
        
        try await sut.execute(actionType: .repeatLast)
        
        XCTAssertEqual(repo.addedScribbles.count, 1)
        XCTAssertEqual(repo.addedScribbles[0], "Repeat last workout")
    }
    
    func test_executeQuickAction_restDay() async throws {
        let repo = MockCanvasRepository()
        let sut = ExecuteQuickActionUseCase(canvasRepository: repo)
        
        try await sut.execute(actionType: .restDay)
        
        XCTAssertEqual(repo.addedScribbles.count, 1)
        XCTAssertEqual(repo.addedScribbles[0], "Today is a rest day")
    }
    
    func test_executeQuickAction_run5k() async throws {
        let repo = MockCanvasRepository()
        let sut = ExecuteQuickActionUseCase(canvasRepository: repo)
        
        try await sut.execute(actionType: .run5k)
        
        XCTAssertEqual(repo.addedScribbles.count, 1)
        XCTAssertEqual(repo.addedScribbles[0], "Logged a 5k run")
    }
    
    // MARK: - ProcessScribbleUseCase
    func test_processScribble() async throws {
        let repo = MockCanvasRepository()
        let sut = ProcessScribbleUseCase(canvasRepository: repo)
        
        try await sut.execute(rawText: "Bench 100x5")
        
        XCTAssertEqual(repo.addedScribbles.count, 1)
        XCTAssertEqual(repo.addedScribbles[0], "Bench 100x5")
    }
    
    // MARK: - ConfirmWorkoutUseCase
    func test_confirmWorkout() async throws {
        let sessionRepo = MockWorkoutSessionRepository()
        let ledgerRepo = MockLedgerRepository()
        let sut = ConfirmWorkoutUseCase(sessionRepository: sessionRepo, ledgerRepository: ledgerRepo)
        
        let exercise = Exercise(id: UUID(), scribbleId: UUID(), canonicalName: "Squat", muscleGroup: "Legs", sets: [
            ExerciseSet(id: UUID(), setNumber: 1, weight: 100, reps: 5)
        ], createdAt: Date())
        let workout = ParsedWorkout(exercises: [exercise], location: "Gym")
        
        try await sut.execute(workout: workout)
        
        XCTAssertTrue(sessionRepo.clearedSession)
        XCTAssertEqual(ledgerRepo.loggedWorkouts.count, 1)
        
        let logged = ledgerRepo.loggedWorkouts[0]
        XCTAssertEqual(logged.location, "Gym")
        XCTAssertEqual(logged.totalVolume, 500.0) // 100 * 5
        XCTAssertEqual(logged.exercises.count, 1)
        XCTAssertEqual(logged.exercises[0].canonicalName, "Squat")
        XCTAssertEqual(logged.exercises[0].sets.count, 1)
        XCTAssertEqual(logged.exercises[0].sets[0].weight, 100)
        XCTAssertEqual(logged.exercises[0].sets[0].reps, 5)
    }
}
