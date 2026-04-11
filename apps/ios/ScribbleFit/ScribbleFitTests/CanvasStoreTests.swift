import XCTest
import Combine
#if SWIFT_PACKAGE
@testable import CoreModel
@testable import FeatureAI
@testable import FeatureScribble
@testable import FeatureWorkouts
@testable import FeatureConfig
@testable import FeatureCanvas
@testable import FeatureInsights
#endif
@testable import ScribbleFit

// MARK: - Mock Repositories

@MainActor
final class MockScribbleRepository: ScribbleRepository {
    var scribbles: [Scribble] = []
    var addedScribbles: [Scribble] = []
    var updatedScribbles: [Scribble] = []
    var deletedIds: [UUID] = []
    var shouldThrow = false

    private var continuations: [AsyncStream<[Scribble]>.Continuation] = []

    func getScribbles(for date: Date) -> AsyncStream<[Scribble]> {
        let currentScribbles = scribbles
        return AsyncStream { continuation in
            continuation.yield(currentScribbles)
            self.continuations.append(continuation)
        }
    }

    func addScribble(_ scribble: Scribble) async throws {
        if shouldThrow { throw MockError.generic }
        addedScribbles.append(scribble)
        scribbles.append(scribble)
    }

    func updateScribble(_ scribble: Scribble) async throws {
        if shouldThrow { throw MockError.generic }
        updatedScribbles.append(scribble)
        if let index = scribbles.firstIndex(where: { $0.id == scribble.id }) {
            scribbles[index] = scribble
        }
    }

    func deleteScribble(id: UUID) async throws {
        if shouldThrow { throw MockError.generic }
        deletedIds.append(id)
        scribbles.removeAll { $0.id == id }
    }

    func getScribble(id: UUID) async throws -> Scribble? {
        scribbles.first { $0.id == id }
    }

    func clearScribbleExercises(scribbleId: UUID) async throws {
        if let index = scribbles.firstIndex(where: { $0.id == scribbleId }) {
            scribbles[index].exercises = []
        }
    }

    func emit(_ snapshot: [Scribble]) {
        for continuation in continuations {
            continuation.yield(snapshot)
        }
    }
}

@MainActor
final class MockWorkoutRepository: WorkoutRepository {
    var workouts: [Workout] = []
    var savedWorkouts: [Workout] = []
    var shouldThrow = false

    func getWorkout(id: UUID) async throws -> Workout? {
        workouts.first { $0.id == id }
    }

    func saveWorkout(_ workout: Workout) async throws {
        if shouldThrow { throw MockError.generic }
        savedWorkouts.append(workout)
        workouts.append(workout)
    }

    func deleteWorkout(id: UUID) async throws {
        workouts.removeAll { $0.id == id }
    }

    func getWorkouts(for date: Date) -> AsyncStream<[Workout]> {
        let current = workouts
        return AsyncStream { continuation in
            continuation.yield(current)
        }
    }
}

@MainActor
final class MockConfigRepository: ConfigRepository {
    private var config = SystemConfig()
    private let subject = CurrentValueSubject<SystemConfig, Never>(SystemConfig())

    var configPublisher: AnyPublisher<SystemConfig, Never> {
        subject.eraseToAnyPublisher()
    }

    func getConfig() -> SystemConfig { config }

    func updateConfig(_ newConfig: SystemConfig) {
        config = newConfig
        subject.send(newConfig)
    }

    func resetConfig() {
        config = SystemConfig()
        subject.send(config)
    }
}

@MainActor
final class MockLLMService: LLMService {
    var parseResult = ParsedWorkoutResult(exercises: [], rawText: "")
    var insights: [AIInsight] = []
    var shouldThrow = false

    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        if shouldThrow { throw MockError.generic }
        return parseResult
    }

    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        if shouldThrow { throw MockError.generic }
        return insights
    }

    func validateApiKey(_ apiKey: String) async throws {}
    func getAvailableModels(apiKey: String) async throws -> [String] { [] }
}

enum MockError: Error {
    case generic
}

// MARK: - Test Fixtures

enum TestFixtures {
    static let setId = UUID()
    static let exerciseId = UUID()
    static let scribbleId = UUID()

    @MainActor
    static func makeExerciseSet(
        id: UUID = setId,
        setNumber: Int = 1,
        weight: Float = 100.0,
        reps: Int = 10
    ) -> ExerciseSet {
        ExerciseSet(id: id, setNumber: setNumber, weight: weight, reps: reps)
    }

    @MainActor
    static func makeExercise(
        id: UUID = exerciseId,
        name: String = "Bench Press",
        muscleGroup: String = "Chest",
        sets: [ExerciseSet]? = nil
    ) -> Exercise {
        Exercise(
            id: id,
            canonicalName: name,
            muscleGroup: muscleGroup,
            sets: sets ?? [makeExerciseSet()]
        )
    }

    @MainActor
    static func makeScribble(
        id: UUID = scribbleId,
        rawText: String = "Bench 100kg 3x10",
        status: ScribbleStatus = .success,
        exercises: [Exercise]? = nil
    ) -> Scribble {
        Scribble(
            id: id,
            rawText: rawText,
            status: status,
            createdAt: Date(),
            exercises: exercises ?? [makeExercise()]
        )
    }
}

// MARK: - CanvasStore Tests

final class CanvasStoreTests: XCTestCase {
    // nonisolated(unsafe) allows these to be set in setUp (which is nonisolated)
    // and accessed from @MainActor test methods safely.
    nonisolated(unsafe) var scribbleRepo: MockScribbleRepository!
    nonisolated(unsafe) var workoutRepo: MockWorkoutRepository!
    nonisolated(unsafe) var configRepo: MockConfigRepository!
    nonisolated(unsafe) var llmService: MockLLMService!
    nonisolated(unsafe) var store: CanvasStore!

    @MainActor
    private static func createDependencies() -> (
        MockScribbleRepository,
        MockWorkoutRepository,
        MockConfigRepository,
        MockLLMService,
        CanvasStore
    ) {
        let scribbleRepo = MockScribbleRepository()
        let workoutRepo = MockWorkoutRepository()
        let configRepo = MockConfigRepository()
        let llmService = MockLLMService()

        let getScribblesUC = GetScribblesForDateUseCase(repository: scribbleRepo)
        let addRawScribbleUC = AddRawScribbleUseCase(repository: scribbleRepo)
        let confirmScribbleUC = ConfirmScribbleUseCase(
            scribbleRepository: scribbleRepo,
            workoutRepository: workoutRepo
        )
        let deleteScribbleUC = DeleteScribbleUseCase(repository: scribbleRepo)
        let parsePendingUC = ParsePendingScribblesUseCase(
            scribbleRepository: scribbleRepo,
            llmProvider: llmService
        )
        let getAIOverviewUC = GetAIOverviewUseCase(
            workoutRepository: workoutRepo,
            llmProvider: llmService
        )

        let store = CanvasStore(
            getScribblesForDateUseCase: getScribblesUC,
            addRawScribbleUseCase: addRawScribbleUC,
            confirmScribbleUseCase: confirmScribbleUC,
            deleteScribbleUseCase: deleteScribbleUC,
            parsePendingScribblesUseCase: parsePendingUC,
            getAIOverviewUseCase: getAIOverviewUC,
            configRepository: configRepo
        )

        return (scribbleRepo, workoutRepo, configRepo, llmService, store)
    }

    override func setUp() async throws {
        let deps = await CanvasStoreTests.createDependencies()
        scribbleRepo = deps.0
        workoutRepo = deps.1
        configRepo = deps.2
        llmService = deps.3
        store = deps.4

        // Allow init tasks (observeScribbles, refreshAIInsights) to settle.
        try await Task.sleep(for: .milliseconds(100))
    }

    override func tearDown() async throws {
        store = nil
        scribbleRepo = nil
        workoutRepo = nil
        configRepo = nil
        llmService = nil
    }

    // MARK: - 1. addScribble clears text after success

    @MainActor
    func testAddScribbleClearsTextOnSuccess() async throws {
        store.state.currentScribbleText = "Squat 140kg 5x5"

        store.onIntent(.addScribble("Squat 140kg 5x5"))

        // Wait for the async Task inside addScribble to complete.
        try await Task.sleep(for: .milliseconds(200))

        XCTAssertEqual(store.state.currentScribbleText, "", "Text should be cleared after a successful add")
        XCTAssertEqual(scribbleRepo.addedScribbles.count, 1, "Repository should have received one scribble")
    }

    // MARK: - 2. clickOnScribble with SUCCESS status sets selectedScribble

    @MainActor
    func testClickOnScribbleWithSuccessStatusSetsSelectedScribble() async {
        let scribble = TestFixtures.makeScribble(status: .success)

        store.onIntent(.clickOnScribble(scribble))

        XCTAssertEqual(store.state.selectedScribble, scribble, "selectedScribble should be set for SUCCESS status")
    }

    // MARK: - 3. clickOnScribble with FAILED status does NOT set selectedScribble

    @MainActor
    func testClickOnScribbleWithFailedStatusDoesNotSetSelectedScribble() async {
        let scribble = TestFixtures.makeScribble(status: .failed)

        store.onIntent(.clickOnScribble(scribble))

        XCTAssertNil(
            store.state.selectedScribble,
            "selectedScribble should NOT be set for FAILED status"
        )
    }

    // MARK: - 4. confirmScribble dismisses dialog on success

    @MainActor
    func testConfirmScribbleDismissesDialogOnSuccess() async throws {
        let scribble = TestFixtures.makeScribble(status: .success)
        store.state.selectedScribble = scribble

        store.onIntent(.confirmScribble(scribble))

        // Wait for the async Task inside confirmScribble to complete.
        try await Task.sleep(for: .milliseconds(200))

        XCTAssertNil(store.state.selectedScribble, "selectedScribble should be nil after confirm succeeds")
        XCTAssertEqual(workoutRepo.savedWorkouts.count, 1, "Workout should have been saved")
    }

    // MARK: - 5. updateExerciseName updates the selectedScribble exercise name

    @MainActor
    func testUpdateExerciseNameUpdatesSelectedScribble() async {
        let exerciseId = TestFixtures.exerciseId
        let scribble = TestFixtures.makeScribble(status: .success)
        store.state.selectedScribble = scribble

        store.onIntent(.updateExerciseName(exerciseId, "Incline Bench Press"))

        let updatedExercise = store.state.selectedScribble?.exercises.first { $0.id == exerciseId }
        XCTAssertEqual(
            updatedExercise?.canonicalName,
            "Incline Bench Press",
            "Exercise name should be updated in selectedScribble"
        )
    }

    // MARK: - 6. updateSetWeight updates weight when valid float

    @MainActor
    func testUpdateSetWeightUpdatesWeightForValidFloat() async {
        let exerciseId = TestFixtures.exerciseId
        let setId = TestFixtures.setId
        let scribble = TestFixtures.makeScribble(status: .success)
        store.state.selectedScribble = scribble

        store.onIntent(.updateSetWeight(exerciseId, setId, "120.5"))

        let updatedSet = store.state.selectedScribble?
            .exercises.first { $0.id == exerciseId }?
            .sets.first { $0.id == setId }
        XCTAssertEqual(updatedSet?.weight, 120.5, "Weight should be updated to 120.5")
    }

    @MainActor
    func testUpdateSetWeightIgnoresInvalidFloat() async {
        let exerciseId = TestFixtures.exerciseId
        let setId = TestFixtures.setId
        let scribble = TestFixtures.makeScribble(status: .success)
        store.state.selectedScribble = scribble

        store.onIntent(.updateSetWeight(exerciseId, setId, "not-a-number"))

        let updatedSet = store.state.selectedScribble?
            .exercises.first { $0.id == exerciseId }?
            .sets.first { $0.id == setId }
        XCTAssertEqual(updatedSet?.weight, 100.0, "Weight should remain unchanged for invalid input")
    }

    // MARK: - 7. updateSetReps updates reps when valid int

    @MainActor
    func testUpdateSetRepsUpdatesRepsForValidInt() async {
        let exerciseId = TestFixtures.exerciseId
        let setId = TestFixtures.setId
        let scribble = TestFixtures.makeScribble(status: .success)
        store.state.selectedScribble = scribble

        store.onIntent(.updateSetReps(exerciseId, setId, "12"))

        let updatedSet = store.state.selectedScribble?
            .exercises.first { $0.id == exerciseId }?
            .sets.first { $0.id == setId }
        XCTAssertEqual(updatedSet?.reps, 12, "Reps should be updated to 12")
    }

    @MainActor
    func testUpdateSetRepsIgnoresInvalidInt() async {
        let exerciseId = TestFixtures.exerciseId
        let setId = TestFixtures.setId
        let scribble = TestFixtures.makeScribble(status: .success)
        store.state.selectedScribble = scribble

        store.onIntent(.updateSetReps(exerciseId, setId, "abc"))

        let updatedSet = store.state.selectedScribble?
            .exercises.first { $0.id == exerciseId }?
            .sets.first { $0.id == setId }
        XCTAssertEqual(updatedSet?.reps, 10, "Reps should remain unchanged for invalid input")
    }

    // MARK: - 8. deleteScribble clears selectedScribble

    @MainActor
    func testDeleteScribbleClearsSelectedScribble() async throws {
        let scribble = TestFixtures.makeScribble(status: .success)
        scribbleRepo.scribbles = [scribble]
        store.state.selectedScribble = scribble

        store.onIntent(.deleteScribble(scribble.id))

        // Wait for the async Task inside deleteScribble to complete.
        try await Task.sleep(for: .milliseconds(200))

        XCTAssertNil(store.state.selectedScribble, "selectedScribble should be nil after deletion")
        XCTAssertTrue(scribbleRepo.deletedIds.contains(scribble.id), "Repository should have received the delete call")
    }
}
