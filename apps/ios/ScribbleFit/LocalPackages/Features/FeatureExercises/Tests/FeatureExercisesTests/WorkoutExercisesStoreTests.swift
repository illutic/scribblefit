import XCTest
import Combine
@testable import FeatureExercises
@testable import CoreModel
@testable import FeatureWorkouts

@MainActor
final class WorkoutExercisesStoreTests: XCTestCase {
    var store: WorkoutExercisesStore!
    var mockRepository: MockWorkoutRepository!
    var mockConfigRepository: MockConfigRepository!
    var workoutId: UUID!
    
    override func setUp() {
        super.setUp()
        workoutId = UUID()
        mockRepository = MockWorkoutRepository()
        mockConfigRepository = MockConfigRepository()
        
        let workout = Workout(
            id: workoutId,
            date: Date(),
            exercises: [
                Exercise(id: UUID(), canonicalName: "Pushups", muscleGroup: "Chest", sets: [])
            ]
        )
        mockRepository.stubbedWorkout = workout
        
        store = WorkoutExercisesStore(
            workoutId: workoutId,
            workoutRepository: mockRepository,
            configRepository: mockConfigRepository,
            calculateWorkoutVolumeUseCase: CalculateWorkoutVolumeUseCase(),
            formatWorkoutSummaryUseCase: FormatWorkoutSummaryUseCase(),
            formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase()
        )
    }
    
    func testInitialState() async {
        XCTAssertTrue(store.state.isLoading)
        XCTAssertEqual(store.state.workoutId, workoutId)
        
        // Wait for observation to trigger
        try? await Task.sleep(nanoseconds: 100_000_000)
        
        XCTAssertFalse(store.state.isLoading)
        XCTAssertEqual(store.state.uiModels.count, 1)
        XCTAssertEqual(store.state.uiModels[0].name, "Pushups")
    }
    
    func testIntentRefresh() async {
        store.onIntent(.refresh)
        XCTAssertTrue(store.state.isLoading)
    }
}

class MockWorkoutRepository: WorkoutRepository {
    var stubbedWorkout: Workout?
    
    func getWorkout(id: UUID) async throws -> Workout? { stubbedWorkout }
    func saveWorkout(_ workout: Workout) async throws {}
    func deleteWorkout(id: UUID) async throws {}
    
    func observeWorkout(id: UUID) -> AsyncStream<Workout?> {
        AsyncStream { continuation in
            continuation.yield(stubbedWorkout)
        }
    }
    
    func getWorkouts(for date: Date) -> AsyncStream<[Workout]> {
        AsyncStream { $0.yield([]) }
    }
    
    func getWorkoutsInRange(startDate: Date, endDate: Date) -> AsyncStream<[Workout]> {
        AsyncStream { $0.yield([]) }
    }
    
    func getWorkoutsWithExercise(exerciseName: String) -> AsyncStream<[Workout]> {
        AsyncStream { $0.yield([]) }
    }
}

class MockConfigRepository: ConfigRepository {
    var config = SystemConfig()
    var configPublisher: AnyPublisher<SystemConfig, Never> {
        Just(config).eraseToAnyPublisher()
    }
    
    func getConfig() -> SystemConfig { config }
    func updateConfig(_ config: SystemConfig) async throws {}
}
