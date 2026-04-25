import XCTest
import Combine
@testable import CoreModel
@testable import FeatureAI
@testable import FeatureScribble
@testable import FeatureConfig
@testable import FeatureCanvas
@testable import FeatureInsights
@testable import FeatureSets
@testable import FeatureExercises

// MARK: - Mock Repositories

@MainActor
final class MockScribbleRepository: ScribbleRepository {
    var scribbles: [Scribble] = []
    var addedScribbles: [Scribble] = []
    var updatedScribbles: [Scribble] = []
    var deletedIds: [UUID] = []
    var shouldThrow = false

    private var continuations: [AsyncStream<[Scribble]>.Continuation] = []

    func observeScribbles(for date: Date) -> AsyncStream<[Scribble]> {
        let currentScribbles = scribbles
        return AsyncStream { continuation in
            continuation.yield(currentScribbles)
            self.continuations.append(continuation)
        }
    }

    func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> {
        let currentScribbles = scribbles
        return AsyncStream { continuation in
            continuation.yield(currentScribbles)
            self.continuations.append(continuation)
        }
    }

    func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]> {
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
        emit(scribbles)
    }

    func updateScribble(_ scribble: Scribble) async throws {
        if shouldThrow { throw MockError.generic }
        updatedScribbles.append(scribble)
        if let index = scribbles.firstIndex(where: { $0.id == scribble.id }) {
            scribbles[index] = scribble
        }
        emit(scribbles)
    }

    func confirmScribble(_ scribble: Scribble) async throws {
        if shouldThrow { throw MockError.generic }
        var completed = scribble
        completed.status = .completed
        try await updateScribble(completed)
    }

    func deleteScribble(id: UUID) async throws {
        if shouldThrow { throw MockError.generic }
        deletedIds.append(id)
        scribbles.removeAll { $0.id == id }
        emit(scribbles)
    }

    func getScribble(id: UUID) async throws -> Scribble? {
        scribbles.first { $0.id == id }
    }

    func clearScribbleExercises(scribbleId: UUID) async throws {
        if let index = scribbles.firstIndex(where: { $0.id == scribbleId }) {
            scribbles[index].exercises = []
        }
        emit(scribbles)
    }

    func emit(_ snapshot: [Scribble]) {
        for continuation in continuations {
            continuation.yield(snapshot)
        }
    }
}

@MainActor
final class MockExerciseRepository: ExerciseRepository {
    func getExercises(query: String) async throws -> [Exercise] { [] }
    func getExercise(id: UUID) async throws -> Exercise? { nil }
    func saveExercise(_ exercise: Exercise) async throws {}
    func saveExercise(_ exercise: Exercise, to scribbleId: UUID) async throws {}
    func updateExercise(_ exercise: Exercise) async throws {}
    func deleteExercise(id: UUID) async throws {}
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

    func generateExerciseInsight(history: String) async throws -> ExercisePerformanceInsight {
        if shouldThrow { throw MockError.generic }
        return ExercisePerformanceInsight(
            estimated1RM: 100.0,
            prDetected: false,
            trendDirection: .improving,
            breakdownText: "Keep it up"
        )
    }

    func isSupported() async -> Bool { true }
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
    nonisolated(unsafe) var scribbleRepo: MockScribbleRepository!
    nonisolated(unsafe) var exerciseRepo: MockExerciseRepository!
    nonisolated(unsafe) var configRepo: MockConfigRepository!
    nonisolated(unsafe) var llmService: MockLLMService!
    nonisolated(unsafe) var store: CanvasStore!

    @MainActor
    private static func createDependencies() -> (
        MockScribbleRepository,
        MockExerciseRepository,
        MockConfigRepository,
        MockLLMService,
        CanvasStore
    ) {
        let scribbleRepo = MockScribbleRepository()
        let exerciseRepo = MockExerciseRepository()
        let configRepo = MockConfigRepository()
        let llmService = MockLLMService()

        let getScribblesUC = GetScribblesForDateUseCase(repository: scribbleRepo)
        let addRawScribbleUC = AddRawScribbleUseCase(repository: scribbleRepo)
        let confirmScribbleUC = ConfirmScribbleUseCase(scribbleRepository: scribbleRepo)
        let removeScribbleUC = RemoveScribbleUseCase(repository: scribbleRepo)
        let deleteScribbleUC = DeleteScribbleUseCase(removeScribbleUseCase: removeScribbleUC)
        let parsePendingUC = ParsePendingScribblesUseCase(
            scribbleRepository: scribbleRepo,
            llmProvider: llmService
        )
        let getAIOverviewUC = GetAIOverviewUseCase(
            scribbleRepository: scribbleRepo,
            llmProvider: llmService
        )
        let manualEditUC = ManualEditScribbleUseCase(scribbleRepository: scribbleRepo)
        let createManualUC = CreateManualScribbleUseCase(scribbleRepository: scribbleRepo)
        let reorderSetsUC = ReorderSetsUseCase()
        let calculateTrendsUC = CalculateTrendsUseCase(exerciseRepository: exerciseRepo)

        let store = CanvasStore(
            getScribblesForDateUseCase: getScribblesUC,
            addRawScribbleUseCase: addRawScribbleUC,
            confirmScribbleUseCase: confirmScribbleUC,
            deleteScribbleUseCase: deleteScribbleUC,
            parsePendingScribblesUseCase: parsePendingUC,
            getAIOverviewUseCase: getAIOverviewUC,
            manualEditScribbleUseCase: manualEditUC,
            createManualScribbleUseCase: createManualUC,
            reorderSetsUseCase: reorderSetsUC,
            calculateTrendsUseCase: calculateTrendsUC,
            configRepository: configRepo
        )

        return (scribbleRepo, exerciseRepo, configRepo, llmService, store)
    }

    override func setUp() async throws {
        let deps = await CanvasStoreTests.createDependencies()
        scribbleRepo = deps.0
        exerciseRepo = deps.1
        configRepo = deps.2
        llmService = deps.3
        store = deps.4

        try await Task.sleep(for: .milliseconds(50))
    }

    override func tearDown() async throws {
        store = nil
        scribbleRepo = nil
        exerciseRepo = nil
        configRepo = nil
        llmService = nil
    }

    @MainActor
    func testAddScribbleClearsTextOnSuccess() async throws {
        store.state.currentScribbleText = "Squat 140kg 5x5"

        store.onIntent(CanvasIntent.addScribble("Squat 140kg 5x5"))

        try await Task.sleep(for: .milliseconds(100))

        XCTAssertEqual(store.state.currentScribbleText, "", "Text should be cleared after a successful add")
        XCTAssertEqual(scribbleRepo.addedScribbles.count, 1, "Repository should have received one scribble")
    }

    @MainActor
    func testClickOnScribbleWithSuccessStatusSetsSelectedScribble() async {
        let scribble = TestFixtures.makeScribble(status: .success)

        store.onIntent(CanvasIntent.clickOnScribble(scribble))

        XCTAssertEqual(store.state.selectedScribble, scribble, "selectedScribble should be set for SUCCESS status")
    }

    @MainActor
    func testClickOnScribbleWithCompletedStatusNavigatesToDetails() async {
        let scribble = TestFixtures.makeScribble(status: .completed)

        store.onIntent(CanvasIntent.clickOnScribble(scribble))

        if case .scribbleDetails(let id) = store.state.navigationState {
            XCTAssertEqual(id, scribble.id)
        } else {
            XCTFail("Should have navigated to scribble details")
        }
    }

    @MainActor
    func testConfirmScribbleDismissesDialogOnSuccess() async throws {
        let scribble = TestFixtures.makeScribble(status: .success)
        store.state.selectedScribble = scribble
        scribbleRepo.scribbles = [scribble]

        store.onIntent(CanvasIntent.confirmScribble(scribble))

        try await Task.sleep(for: .milliseconds(100))

        XCTAssertNil(store.state.selectedScribble, "selectedScribble should be nil after confirm succeeds")
        XCTAssertEqual(scribbleRepo.updatedScribbles.first?.status, .completed, "Scribble status should be completed")
    }

    @MainActor
    func testUpdateExerciseNameUpdatesSelectedScribble() async throws {
        let exerciseId = TestFixtures.exerciseId
        let scribble = TestFixtures.makeScribble(status: .success)
        store.state.selectedScribble = scribble
        scribbleRepo.scribbles = [scribble]

        store.onIntent(CanvasIntent.updateExerciseName(exerciseId, "Incline Bench Press"))

        try await Task.sleep(for: .milliseconds(50))

        let updatedExercise = store.state.selectedScribble?.exercises.first { $0.id == exerciseId }
        XCTAssertEqual(
            updatedExercise?.canonicalName,
            "Incline Bench Press",
            "Exercise name should be updated in selectedScribble"
        )
    }

    @MainActor
    func testDeleteScribbleClearsSelectedScribble() async throws {
        let scribble = TestFixtures.makeScribble(status: .success)
        scribbleRepo.scribbles = [scribble]
        store.state.selectedScribble = scribble

        store.onIntent(CanvasIntent.deleteScribble(scribble.id))

        try await Task.sleep(for: .milliseconds(100))

        XCTAssertNil(store.state.selectedScribble, "selectedScribble should be nil after deletion")
        XCTAssertTrue(scribbleRepo.deletedIds.contains(scribble.id), "Repository should have received the delete call")
    }
}
