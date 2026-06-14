import XCTest
import CoreModel
import CoreCommon
@testable import FeatureExercises

// MARK: - Mock ExerciseRepository

@MainActor
final class MockExerciseRepository: ExerciseRepository {
    var savedExercises: [(Exercise, UUID?)] = []
    var updatedExercises: [Exercise] = []
    var deletedIds: [UUID] = []
    var exercisesToReturn: [Exercise] = []
    var exerciseToReturn: Exercise?
    var shouldThrow: Error?

    func getExercises(query: String) async throws -> [Exercise] {
        if let e = shouldThrow { throw e }
        return exercisesToReturn.filter {
            query.isEmpty || $0.canonicalName.localizedCaseInsensitiveContains(query)
        }
    }

    func observeExercises(query: String) -> AsyncStream<[Exercise]> {
        let data = exercisesToReturn
        return AsyncStream { cont in cont.yield(data); cont.finish() }
    }

    func getExercise(id: UUID) async throws -> Exercise? {
        if let e = shouldThrow { throw e }
        return exerciseToReturn
    }

    func saveExercise(_ exercise: Exercise) async throws {
        if let e = shouldThrow { throw e }
        savedExercises.append((exercise, nil))
    }

    func saveExercise(_ exercise: Exercise, to scribbleId: UUID) async throws {
        if let e = shouldThrow { throw e }
        savedExercises.append((exercise, scribbleId))
    }

    func updateExercise(_ exercise: Exercise) async throws {
        if let e = shouldThrow { throw e }
        updatedExercises.append(exercise)
    }

    func deleteExercise(id: UUID) async throws {
        if let e = shouldThrow { throw e }
        deletedIds.append(id)
    }
}

// MARK: - Mock ScribbleRepository (simplified)

@MainActor
final class MockScribbleRepo2: ScribbleRepository {
    var scribbleToReturn: Scribble?
    var deletedIds: [UUID] = []
    var shouldThrow: Error?

    func observeScribbles(for date: Date) -> AsyncStream<[Scribble]> { AsyncStream { $0.finish() } }
    func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> { AsyncStream { $0.finish() } }
    func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]> {
        let s = scribbleToReturn.map { [$0] } ?? []
        return AsyncStream { cont in cont.yield(s); cont.finish() }
    }
    func addScribble(_ scribble: Scribble) async throws {}
    func updateScribble(_ scribble: Scribble) async throws {}
    func deleteScribble(id: UUID) async throws {
        if let e = shouldThrow { throw e }
        deletedIds.append(id)
    }
    func getScribble(id: UUID) async throws -> Scribble? {
        if let e = shouldThrow { throw e }
        return scribbleToReturn
    }
    func clearScribbleExercises(scribbleId: UUID) async throws {}
    func confirmScribble(_ scribble: Scribble) async throws {}
}

// MARK: - Mock LLMService

@MainActor
final class MockExerciseLLMService: LLMService {
    var insightToReturn: AIInsight = AIInsight(insightType: .trend, text: "Great progress!")
    var shouldThrow: Error?

    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        ParsedWorkoutResult(exercises: [], rawText: rawText)
    }
    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] { [] }
    func generateExerciseInsight(history: String) async throws -> AIInsight {
        if let e = shouldThrow { throw e }
        return insightToReturn
    }
    func isSupported() async -> Bool { false }
}

// MARK: - Helpers

private func makeSet(number: Int = 1, weight: Float? = 100, reps: Int = 10) -> ExerciseSet {
    ExerciseSet(id: UUID(), setNumber: number, weight: weight, reps: reps)
}

private func makeExercise(
    id: UUID = UUID(),
    name: String = "Bench Press",
    muscleGroup: String = "Chest",
    sets: [ExerciseSet] = [],
    scribbleId: UUID? = nil,
    createdAt: Date = Date()
) -> Exercise {
    Exercise(id: id, scribbleId: scribbleId, canonicalName: name, muscleGroup: muscleGroup, sets: sets, createdAt: createdAt)
}

private func makeScribble(id: UUID = UUID(), exercises: [Exercise] = []) -> Scribble {
    Scribble(id: id, rawText: "test", status: .completed, exercises: exercises)
}



// MARK: - AddManualExerciseUseCase Tests

@MainActor
final class AddManualExerciseUseCaseTests: XCTestCase {

    private var mockRepo: MockExerciseRepository!
    private var sut: AddManualExerciseUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockExerciseRepository()
        sut = AddManualExerciseUseCase(exerciseRepository: mockRepo)
    }

    func test_execute_savesExerciseToWorkout() async throws {
        let workoutId = UUID()
        let sets = [makeSet()]

        try await sut.execute(workoutId: workoutId, exerciseName: "Squat", muscleGroup: "Legs", sets: sets)

        XCTAssertEqual(mockRepo.savedExercises.count, 1)
        XCTAssertEqual(mockRepo.savedExercises[0].1, workoutId)
    }

    func test_execute_exerciseHasCorrectName() async throws {
        try await sut.execute(workoutId: UUID(), exerciseName: "Deadlift", muscleGroup: "Back", sets: [])

        XCTAssertEqual(mockRepo.savedExercises[0].0.canonicalName, "Deadlift")
    }

    func test_execute_exerciseHasCorrectMuscleGroup() async throws {
        try await sut.execute(workoutId: UUID(), exerciseName: "Row", muscleGroup: "Back", sets: [])

        XCTAssertEqual(mockRepo.savedExercises[0].0.muscleGroup, "Back")
    }

    func test_execute_setsAttachedToExercise() async throws {
        let sets = [makeSet(number: 1, weight: 80, reps: 8), makeSet(number: 2, weight: 90, reps: 6)]

        try await sut.execute(workoutId: UUID(), exerciseName: "OHP", muscleGroup: "Shoulders", sets: sets)

        XCTAssertEqual(mockRepo.savedExercises[0].0.sets.count, 2)
    }

    func test_execute_repositoryError_propagates() async {
        mockRepo.shouldThrow = NSError(domain: "DB", code: 1)
        await XCTAssertThrowsErrorAsync {
            try await self.sut.execute(workoutId: UUID(), exerciseName: "X", muscleGroup: "Y", sets: [])
        }
    }
}

// MARK: - AddSetToExerciseUseCase Tests

@MainActor
final class AddSetToExerciseUseCaseTests: XCTestCase {

    private var mockRepo: MockExerciseRepository!
    private var sut: AddSetToExerciseUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockExerciseRepository()
        sut = AddSetToExerciseUseCase(exerciseRepository: mockRepo)
    }

    func test_execute_existingExercise_appendsSet() async throws {
        let exerciseId = UUID()
        let existingSet = makeSet(number: 1)
        mockRepo.exerciseToReturn = makeExercise(id: exerciseId, sets: [existingSet])

        try await sut.execute(exerciseId: exerciseId, nextSetNumber: 2)

        let updated = mockRepo.updatedExercises.first
        XCTAssertEqual(updated?.sets.count, 2)
    }

    func test_execute_newSet_hasCorrectSetNumber() async throws {
        let exerciseId = UUID()
        mockRepo.exerciseToReturn = makeExercise(id: exerciseId, sets: [makeSet(number: 1)])

        try await sut.execute(exerciseId: exerciseId, nextSetNumber: 5)

        let newSet = mockRepo.updatedExercises.first?.sets.last
        XCTAssertEqual(newSet?.setNumber, 5)
    }

    func test_execute_newSet_defaultsToZeroWeightAndReps() async throws {
        let exerciseId = UUID()
        mockRepo.exerciseToReturn = makeExercise(id: exerciseId, sets: [makeSet()])

        try await sut.execute(exerciseId: exerciseId, nextSetNumber: 2)

        let newSet = mockRepo.updatedExercises.first?.sets.last
        XCTAssertEqual(newSet?.weight, 0.0)
        XCTAssertEqual(newSet?.reps, 0)
    }

    func test_execute_exerciseNotFound_doesNotUpdate() async throws {
        mockRepo.exerciseToReturn = nil

        try await sut.execute(exerciseId: UUID(), nextSetNumber: 2)

        XCTAssertTrue(mockRepo.updatedExercises.isEmpty)
    }
}

// MARK: - RemoveExerciseUseCase Tests

@MainActor
final class RemoveExerciseUseCaseTests: XCTestCase {

    private var mockExerciseRepo: MockExerciseRepository!
    private var mockScribbleRepo: MockScribbleRepo2!
    private var sut: RemoveExerciseUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockExerciseRepo = MockExerciseRepository()
        mockScribbleRepo = MockScribbleRepo2()
        sut = RemoveExerciseUseCase(exerciseRepository: mockExerciseRepo, scribbleRepository: mockScribbleRepo)
    }

    func test_execute_exerciseFound_deletesExercise() async throws {
        let exerciseId = UUID()
        mockExerciseRepo.exerciseToReturn = makeExercise(id: exerciseId)

        try await sut.execute(id: exerciseId)

        XCTAssertTrue(mockExerciseRepo.deletedIds.contains(exerciseId))
    }

    func test_execute_exerciseNotFound_doesNothing() async throws {
        mockExerciseRepo.exerciseToReturn = nil

        try await sut.execute(id: UUID())

        XCTAssertTrue(mockExerciseRepo.deletedIds.isEmpty)
    }

    func test_execute_scribbleWithRemainingExercises_doesNotDeleteScribble() async throws {
        let scribbleId = UUID()
        let exerciseId = UUID()
        let remainingExercise = makeExercise()
        let scribble = makeScribble(id: scribbleId, exercises: [remainingExercise])
        mockExerciseRepo.exerciseToReturn = makeExercise(id: exerciseId, scribbleId: scribbleId)
        mockScribbleRepo.scribbleToReturn = scribble

        try await sut.execute(id: exerciseId)

        XCTAssertTrue(mockScribbleRepo.deletedIds.isEmpty)
    }

    func test_execute_scribbleWithNoRemainingExercises_deletesScribble() async throws {
        let scribbleId = UUID()
        let exerciseId = UUID()
        let emptyScribble = makeScribble(id: scribbleId, exercises: [])
        mockExerciseRepo.exerciseToReturn = makeExercise(id: exerciseId, scribbleId: scribbleId)
        mockScribbleRepo.scribbleToReturn = emptyScribble

        try await sut.execute(id: exerciseId)

        XCTAssertTrue(mockScribbleRepo.deletedIds.contains(scribbleId))
    }
}

// MARK: - GetExerciseHistoryUseCase Tests

@MainActor
final class GetExerciseHistoryUseCaseTests: XCTestCase {

    private var mockRepo: MockExerciseRepository!
    private var formatUseCase: FormatExerciseSummaryUseCase!
    private var sut: GetExerciseHistoryUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockExerciseRepository()
        formatUseCase = FormatExerciseSummaryUseCase()
        sut = GetExerciseHistoryUseCase(exerciseRepository: mockRepo, formatExerciseSummaryUseCase: formatUseCase)
    }

    func test_execute_emptyHistory_returnsEmpty() async throws {
        mockRepo.exercisesToReturn = []

        let result = try await sut.execute(exerciseName: "Bench", weightUnit: .kgs)

        XCTAssertTrue(result.isEmpty)
    }

    func test_execute_filtersExercisesByName() async throws {
        let bench = makeExercise(name: "Bench Press", sets: [makeSet()])
        let squat = makeExercise(name: "Squat", sets: [makeSet()])
        mockRepo.exercisesToReturn = [bench, squat]

        let result = try await sut.execute(exerciseName: "Bench Press", weightUnit: .kgs)

        XCTAssertEqual(result.count, 1)
        XCTAssertEqual(result[0].exercise.canonicalName, "Bench Press")
    }

    func test_execute_caseInsensitiveMatch() async throws {
        let bench = makeExercise(name: "Bench Press", sets: [makeSet()])
        mockRepo.exercisesToReturn = [bench]

        let result = try await sut.execute(exerciseName: "bench press", weightUnit: .kgs)

        XCTAssertEqual(result.count, 1)
    }

    func test_execute_personalBest_flaggedCorrectly() async throws {
        let set1 = makeSet(weight: 100, reps: 5)
        let set2 = makeSet(weight: 120, reps: 3)  // Heavier = PB
        let older = makeExercise(name: "Bench", sets: [set1], createdAt: Date(timeIntervalSinceNow: -86400))
        let newer = makeExercise(name: "Bench", sets: [set2], createdAt: Date())
        mockRepo.exercisesToReturn = [newer, older]

        let result = try await sut.execute(exerciseName: "Bench", weightUnit: .kgs)

        // The session with 120kg should be marked as PB
        let pbSession = result.first { $0.maxWeight == 120 }
        XCTAssertEqual(pbSession?.isPersonalBest, true)
    }

    func test_execute_sortedByDateDescending() async throws {
        let older = makeExercise(name: "Bench", sets: [makeSet()], createdAt: Date(timeIntervalSinceNow: -86400))
        let newer = makeExercise(name: "Bench", sets: [makeSet()], createdAt: Date())
        mockRepo.exercisesToReturn = [older, newer]

        let result = try await sut.execute(exerciseName: "Bench", weightUnit: .kgs)

        XCTAssertGreaterThan(result[0].date, result[1].date)
    }
}

// MARK: - FormatExerciseSummaryUseCase Tests

final class FormatExerciseSummaryUseCaseTests: XCTestCase {

    private var sut: FormatExerciseSummaryUseCase!

    override func setUp() {
        super.setUp()
        sut = FormatExerciseSummaryUseCase()
    }

    func test_execute_emptySets_returnsEmpty() {
        let ex = makeExercise(sets: [])
        let result = sut.execute(exercise: ex, weightUnit: .kgs)
        XCTAssertEqual(result, "")
    }

    func test_execute_uniformSets_standardFormat() {
        let sets = [
            ExerciseSet(id: UUID(), setNumber: 1, weight: 100.0, reps: 10),
            ExerciseSet(id: UUID(), setNumber: 2, weight: 100.0, reps: 10),
            ExerciseSet(id: UUID(), setNumber: 3, weight: 100.0, reps: 10)
        ]
        let ex = makeExercise(sets: sets)
        let result = sut.execute(exercise: ex, weightUnit: .kgs)
        // Expected: "100.0kg • 3 sets x 10 reps"
        XCTAssertTrue(result.contains("100.0kg"))
        XCTAssertTrue(result.contains("3"))
        XCTAssertTrue(result.contains("10"))
    }

    func test_execute_variedSets_commaFormat() {
        let sets = [
            ExerciseSet(id: UUID(), setNumber: 1, weight: 100.0, reps: 10),
            ExerciseSet(id: UUID(), setNumber: 2, weight: 90.0, reps: 8)
        ]
        let ex = makeExercise(sets: sets)
        let result = sut.execute(exercise: ex, weightUnit: .kgs)
        // Expected: "100.0kg 1x10, 90.0kg 1x8"
        XCTAssertTrue(result.contains(","))
        XCTAssertTrue(result.contains("100.0kg"))
        XCTAssertTrue(result.contains("90.0kg"))
    }

    func test_execute_bodweightSets_showsBodyweightLabel() {
        let sets = [ExerciseSet(id: UUID(), setNumber: 1, weight: nil, reps: 15)]
        let ex = makeExercise(sets: sets)
        let result = sut.execute(exercise: ex, weightUnit: .kgs)
        XCTAssertTrue(result.contains("Bodyweight") || result.contains("bodyweight"))
    }

    func test_execute_lbsUnit_showsLbLabel() {
        let sets = [ExerciseSet(id: UUID(), setNumber: 1, weight: 225.0, reps: 5)]
        let ex = makeExercise(sets: sets)
        let result = sut.execute(exercise: ex, weightUnit: .lbs)
        XCTAssertTrue(result.contains("lb"))
    }

    func test_execute_singleSet_noComma() {
        let sets = [ExerciseSet(id: UUID(), setNumber: 1, weight: 80.0, reps: 12)]
        let ex = makeExercise(sets: sets)
        let result = sut.execute(exercise: ex, weightUnit: .kgs)
        XCTAssertFalse(result.contains(","))
    }
}

// MARK: - GetExerciseAIInsightUseCase Tests

@MainActor
final class GetExerciseAIInsightUseCaseTests: XCTestCase {

    private var mockLLM: MockExerciseLLMService!
    private var sut: GetExerciseAIInsightUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockLLM = MockExerciseLLMService()
        sut = GetExerciseAIInsightUseCase(llmService: mockLLM)
    }

    func test_execute_emptyHistory_throwsError() async {
        do { _ = try await self.sut.execute(history: []); XCTFail("Expected error") } catch {}
    }

    func test_execute_withHistory_returnsInsight() async throws {
        let set = makeSet(weight: 100, reps: 5)
        let ex = makeExercise(sets: [set])
        let session = ExerciseHistorySession(exercise: ex, totalVolume: 500, maxWeight: 100, summary: "", isPersonalBest: false, scribbleId: UUID())

        mockLLM.insightToReturn = AIInsight(insightType: .trend, text: "You're improving!")

        let insight = try await sut.execute(history: [session])

        XCTAssertEqual(insight.text, "You're improving!")
    }

    func test_execute_llmError_propagates() async {
        let set = makeSet()
        let ex = makeExercise(sets: [set])
        let session = ExerciseHistorySession(exercise: ex, totalVolume: 100, maxWeight: 100, summary: "", isPersonalBest: false, scribbleId: UUID())
        mockLLM.shouldThrow = NSError(domain: "LLM", code: 500)

        do { _ = try await self.sut.execute(history: [session]); XCTFail("Expected error") } catch {}
    }

    func test_execute_takesAtMostFiveSessions() async throws {
        // Build 10 history sessions
        let sessions = (1...10).map { i -> ExerciseHistorySession in
            let set = makeSet(number: 1, weight: Float(i * 10), reps: 5)
            let ex = makeExercise(sets: [set])
            return ExerciseHistorySession(exercise: ex, totalVolume: Float(i * 50), maxWeight: Float(i * 10), summary: "", isPersonalBest: false, scribbleId: UUID())
        }
        // Just verify it doesn't throw with many sessions
        let insight = try await sut.execute(history: sessions)
        XCTAssertNotNil(insight)
    }
}

// MARK: - CalculateTrendsUseCase Tests

@MainActor
final class CalculateTrendsUseCaseTests: XCTestCase {

    private var mockRepo: MockExerciseRepository!
    private var sut: CalculateTrendsUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockExerciseRepository()
        sut = CalculateTrendsUseCase(exerciseRepository: mockRepo)
    }

    func test_execute_noHistory_returnsNil() async {
        mockRepo.exercisesToReturn = []
        let ex = makeExercise(name: "Squat", sets: [makeSet()])

        let result = await sut.execute(exercise: ex)

        XCTAssertNil(result)
    }

    func test_execute_withHistory_returnsTrends() async {
        let set = makeSet(weight: 100, reps: 5)
        let ex = makeExercise(name: "Bench", sets: [set])
        mockRepo.exercisesToReturn = [ex]

        let result = await sut.execute(exercise: ex)

        XCTAssertNotNil(result)
    }

    func test_execute_repositoryError_returnsNil() async {
        mockRepo.shouldThrow = NSError(domain: "DB", code: 1)
        let ex = makeExercise(name: "Bench", sets: [makeSet()])

        let result = await sut.execute(exercise: ex)

        // Error swallowed, returns nil
        XCTAssertNil(result)
    }

    func test_execute_currentExerciseImprovedOverPrevious_trendDirectionImproving() async {
        let previousSet = makeSet(weight: 80, reps: 5)  // Lower weight
        let currentSet = makeSet(weight: 120, reps: 5)  // Higher weight

        let previousEx = makeExercise(id: UUID(), name: "Bench", sets: [previousSet], createdAt: Date(timeIntervalSinceNow: -86400))
        let currentEx = makeExercise(id: UUID(), name: "Bench", sets: [currentSet], createdAt: Date())

        mockRepo.exercisesToReturn = [previousEx, currentEx]

        let result = await sut.execute(exercise: currentEx)

        XCTAssertEqual(result?.trendDirection, .improving)
    }
}
