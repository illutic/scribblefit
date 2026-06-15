import XCTest
import CoreModel
@testable import FeatureScribble

// MARK: - Mock ScribbleRepository

@MainActor
final class MockScribbleRepository: ScribbleRepository {

    // Stored calls
    var addedScribbles: [Scribble] = []
    var updatedScribbles: [Scribble] = []
    var deletedIds: [UUID] = []
    var clearedExerciseScribbleIds: [UUID] = []
    var confirmedScribbles: [Scribble] = []

    // Stubs
    var scribbleToReturn: Scribble?
    var shouldThrowOnAdd: Error?
    var shouldThrowOnUpdate: Error?
    var shouldThrowOnDelete: Error?
    var shouldThrowOnGet: Error?
    var scribblesToStream: [Scribble] = []

    func observeScribbles(for date: Date) -> AsyncStream<[Scribble]> {
        let scribbles = scribblesToStream
        return AsyncStream { continuation in
            continuation.yield(scribbles)
            continuation.finish()
        }
    }

    func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> {
        let scribbles = scribblesToStream
        return AsyncStream { continuation in
            continuation.yield(scribbles)
            continuation.finish()
        }
    }

    func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]> {
        let scribbles = scribblesToStream
        return AsyncStream { continuation in
            continuation.yield(scribbles)
            continuation.finish()
        }
    }

    func addScribble(_ scribble: Scribble) async throws {
        if let error = shouldThrowOnAdd { throw error }
        addedScribbles.append(scribble)
    }

    func updateScribble(_ scribble: Scribble) async throws {
        if let error = shouldThrowOnUpdate { throw error }
        updatedScribbles.append(scribble)
    }

    func deleteScribble(id: UUID) async throws {
        if let error = shouldThrowOnDelete { throw error }
        deletedIds.append(id)
    }

    func getScribble(id: UUID) async throws -> Scribble? {
        if let error = shouldThrowOnGet { throw error }
        return scribbleToReturn
    }

    func clearScribbleExercises(scribbleId: UUID) async throws {
        clearedExerciseScribbleIds.append(scribbleId)
    }

    func confirmScribble(_ scribble: Scribble) async throws {
        if let error = shouldThrowOnUpdate { throw error }
        confirmedScribbles.append(scribble)
    }
}

// MARK: - Mock LLMService

@MainActor
final class MockLLMService: LLMService {
    var parsedResult: ParsedWorkoutResult?
    var shouldThrowOnParse: Error?
    var generatedInsights: [AIInsight] = []
    var generatedExerciseInsight: AIInsight = AIInsight(insightType: .trend, text: "Mock insight")

    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        if let error = shouldThrowOnParse { throw error }
        return parsedResult ?? ParsedWorkoutResult(exercises: [], rawText: rawText)
    }

    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        return generatedInsights
    }

    func generateExerciseInsight(history: String) async throws -> AIInsight {
        return generatedExerciseInsight
    }

    func isSupported() async -> Bool { return false }
}

// MARK: - Helpers

private func makeScribble(
    id: UUID = UUID(),
    rawText: String = "bench press 3x10 100kg",
    status: ScribbleStatus = .success,
    createdAt: Date = Date(),
    exercises: [Exercise] = []
) -> Scribble {
    Scribble(id: id, rawText: rawText, status: status, createdAt: createdAt, exercises: exercises)
}

private func makeExercise(
    id: UUID = UUID(),
    name: String = "Bench Press",
    muscleGroup: String = "Chest"
) -> Exercise {
    Exercise(id: id, canonicalName: name, muscleGroup: muscleGroup)
}

private func makeSet(number: Int = 1, weight: Float? = 100, reps: Int = 10) -> ExerciseSet {
    ExerciseSet(id: UUID(), setNumber: number, weight: weight, reps: reps)
}

// MARK: - RemoveScribbleUseCase Tests

@MainActor
final class RemoveScribbleUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var sut: RemoveScribbleUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockScribbleRepository()
        sut = RemoveScribbleUseCase(repository: mockRepo)
    }

    func test_execute_callsDeleteWithCorrectId() async throws {
        let id = UUID()
        try await sut.execute(id: id)
        XCTAssertEqual(mockRepo.deletedIds, [id])
    }

    func test_execute_propagatesError() async {
        let id = UUID()
        mockRepo.shouldThrowOnDelete = NSError(domain: "Test", code: 1)
        do { try await self.sut.execute(id: id); XCTFail("Expected error") } catch {}
    }

    func test_execute_calledTwice_deletesTwice() async throws {
        let id1 = UUID()
        let id2 = UUID()
        try await sut.execute(id: id1)
        try await sut.execute(id: id2)
        XCTAssertEqual(mockRepo.deletedIds, [id1, id2])
    }
}

// MARK: - GetScribblesForDateUseCase Tests

@MainActor
final class GetScribblesForDateUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var sut: GetScribblesForDateUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockScribbleRepository()
        sut = GetScribblesForDateUseCase(repository: mockRepo)
    }

    func test_execute_returnsScribblesFromRepository() async {
        let scribble = makeScribble()
        mockRepo.scribblesToStream = [scribble]

        let stream = sut.execute(date: Date())
        var received: [Scribble] = []
        for await batch in stream {
            received = batch
            break
        }

        XCTAssertEqual(received.count, 1)
        XCTAssertEqual(received[0].id, scribble.id)
    }

    func test_execute_emptyDate_returnsEmpty() async {
        mockRepo.scribblesToStream = []
        let stream = sut.execute(date: Date())
        var received: [Scribble] = []
        for await batch in stream {
            received = batch
            break
        }
        XCTAssertTrue(received.isEmpty)
    }
}

// MARK: - CreateManualScribbleUseCase Tests

@MainActor
final class CreateManualScribbleUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var sut: CreateManualScribbleUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockScribbleRepository()
        sut = CreateManualScribbleUseCase(scribbleRepository: mockRepo)
    }

    func test_execute_addsScribbleWithSuccessStatus() async throws {
        try await sut.execute(
            exerciseName: "Squat",
            muscleGroup: "Legs",
            sets: [makeSet()],
            date: Date()
        )

        XCTAssertEqual(mockRepo.addedScribbles.count, 1)
        XCTAssertEqual(mockRepo.addedScribbles[0].status, .success)
    }

    func test_execute_rawTextContainsExerciseName() async throws {
        try await sut.execute(
            exerciseName: "DeadLift",
            muscleGroup: "Back",
            sets: [],
            date: Date()
        )

        let rawText = mockRepo.addedScribbles[0].rawText
        XCTAssertTrue(rawText.contains("DeadLift"))
    }

    func test_execute_usesManualEntryFormat() async throws {
        try await sut.execute(
            exerciseName: "Row",
            muscleGroup: "Back",
            sets: [],
            date: Date()
        )

        let rawText = mockRepo.addedScribbles[0].rawText
        XCTAssertTrue(rawText.hasPrefix("Manual Entry:"))
    }

    func test_execute_setsAreAttachedToExercise() async throws {
        let sets = [makeSet(number: 1, weight: 50, reps: 8), makeSet(number: 2, weight: 60, reps: 6)]

        try await sut.execute(
            exerciseName: "BenchPress",
            muscleGroup: "Chest",
            sets: sets,
            date: Date()
        )

        XCTAssertEqual(mockRepo.addedScribbles[0].exercises[0].sets.count, 2)
    }

    func test_execute_propagatesRepositoryError() async {
        mockRepo.shouldThrowOnAdd = NSError(domain: "DB", code: 99)
        do { try await self.sut.execute(exerciseName: "x", muscleGroup: "y", sets: [], date: Date()); XCTFail("Expected error") } catch {}
    }

    func test_execute_dateIsPreserved() async throws {
        let fixedDate = Date(timeIntervalSince1970: 1_000_000)
        try await sut.execute(
            exerciseName: "Curl",
            muscleGroup: "Arms",
            sets: [],
            date: fixedDate
        )

        XCTAssertEqual(mockRepo.addedScribbles[0].createdAt, fixedDate)
    }
}

// MARK: - EditScribbleUseCase Tests

@MainActor
final class EditScribbleUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var sut: EditScribbleUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockScribbleRepository()
        sut = EditScribbleUseCase(repository: mockRepo)
    }

    func test_execute_updatesScribbleText() async throws {
        let id = UUID()
        mockRepo.scribbleToReturn = makeScribble(id: id, status: .success)

        try await sut.execute(id: id, newText: "New workout text")

        XCTAssertEqual(mockRepo.updatedScribbles.last?.rawText, "New workout text")
    }

    func test_execute_setsStatusToPending() async throws {
        let id = UUID()
        mockRepo.scribbleToReturn = makeScribble(id: id, status: .success)

        try await sut.execute(id: id, newText: "Updated")

        XCTAssertEqual(mockRepo.updatedScribbles.last?.status, .pending)
    }

    func test_execute_clearsExercisesBeforeUpdate() async throws {
        let id = UUID()
        mockRepo.scribbleToReturn = makeScribble(id: id)

        try await sut.execute(id: id, newText: "new text")

        XCTAssertTrue(mockRepo.clearedExerciseScribbleIds.contains(id))
    }

    func test_execute_emptyText_throwsError() async {
        let id = UUID()
        mockRepo.scribbleToReturn = makeScribble(id: id)

        do { try await self.sut.execute(id: id, newText: "   "); XCTFail("Expected error") } catch {}
    }

    func test_execute_blankText_throwsError() async {
        let id = UUID()
        mockRepo.scribbleToReturn = makeScribble(id: id)

        do { try await self.sut.execute(id: id, newText: ""); XCTFail("Expected error") } catch {}
    }

    func test_execute_notFound_throwsNotFoundError() async {
        let id = UUID()
        mockRepo.scribbleToReturn = nil

        do { try await self.sut.execute(id: id, newText: "valid text"); XCTFail("Expected error") } catch {}
    }
}

// MARK: - ParsePendingScribblesUseCase Tests

@MainActor
final class ParsePendingScribblesUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var mockLLM: MockLLMService!
    private var sut: ParsePendingScribblesUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockScribbleRepository()
        mockLLM = MockLLMService()
        sut = ParsePendingScribblesUseCase(scribbleRepository: mockRepo, llmProvider: mockLLM)
    }

    // MARK: parseSingleScribble happy path

    func test_parseSingleScribble_setsStatusToParsingThenSuccess() async throws {
        let id = UUID()
        var scribble = makeScribble(id: id, status: .pending)
        mockRepo.scribbleToReturn = scribble
        let exercise = makeExercise()
        mockLLM.parsedResult = ParsedWorkoutResult(exercises: [exercise], rawText: scribble.rawText)

        try await sut.parseSingleScribble(id: id)

        // First update sets status to .parsing, second update sets status to .success
        XCTAssertEqual(mockRepo.updatedScribbles[0].status, .parsing)
        XCTAssertEqual(mockRepo.updatedScribbles[1].status, .success)
    }

    func test_parseSingleScribble_assignsExercisesOnSuccess() async throws {
        let id = UUID()
        mockRepo.scribbleToReturn = makeScribble(id: id, status: .pending)
        let exercise = makeExercise()
        mockLLM.parsedResult = ParsedWorkoutResult(exercises: [exercise], rawText: "test")

        try await sut.parseSingleScribble(id: id)

        XCTAssertFalse(mockRepo.updatedScribbles[1].exercises.isEmpty)
    }

    func test_parseSingleScribble_llmFailure_setsStatusToFailed() async {
        let id = UUID()
        mockRepo.scribbleToReturn = makeScribble(id: id, status: .pending)
        mockLLM.shouldThrowOnParse = NSError(domain: "LLM", code: 500)

        do { try await self.sut.parseSingleScribble(id: id); XCTFail("Expected error") } catch {}

        let failedUpdate = mockRepo.updatedScribbles.last
        XCTAssertEqual(failedUpdate?.status, .failed)
    }

    func test_parseSingleScribble_notFound_returnsImmediately() async throws {
        mockRepo.scribbleToReturn = nil
        // Should not throw
        try await sut.parseSingleScribble(id: UUID())
        XCTAssertTrue(mockRepo.updatedScribbles.isEmpty)
    }

    // MARK: execute (noop, documents intent)

    func test_execute_doesNotCrash() async {
        await sut.execute(date: Date())
        // The execute body is currently a noop stub; just verifying it doesn't crash
    }
}

// MARK: - ManualEditScribbleUseCase Tests

@MainActor
final class ManualEditScribbleUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var sut: ManualEditScribbleUseCase!

    private let scribbleId = UUID()
    private let exerciseId = UUID()
    private let setId = UUID()

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockScribbleRepository()
        sut = ManualEditScribbleUseCase(scribbleRepository: mockRepo)
    }

    private func makeFullScribble() -> Scribble {
        let set = ExerciseSet(id: setId, setNumber: 1, weight: 100, reps: 10)
        let exercise = Exercise(id: exerciseId, canonicalName: "Squat", muscleGroup: "Legs", sets: [set])
        return Scribble(id: scribbleId, rawText: "squat", status: .success, exercises: [exercise])
    }

    // MARK: updateExerciseName

    func test_updateExerciseName_renamesExercise() async throws {
        mockRepo.scribbleToReturn = makeFullScribble()

        try await sut.updateExerciseName(scribbleId: scribbleId, exerciseId: exerciseId, newName: "Leg Press")

        let updatedExercise = mockRepo.updatedScribbles[0].exercises.first { $0.id == exerciseId }
        XCTAssertEqual(updatedExercise?.canonicalName, "Leg Press")
    }

    func test_updateExerciseName_notFound_returnsWithoutUpdate() async throws {
        mockRepo.scribbleToReturn = nil
        try await sut.updateExerciseName(scribbleId: scribbleId, exerciseId: exerciseId, newName: "X")
        XCTAssertTrue(mockRepo.updatedScribbles.isEmpty)
    }

    // MARK: updateSetWeight

    func test_updateSetWeight_updatesCorrectSet() async throws {
        mockRepo.scribbleToReturn = makeFullScribble()

        try await sut.updateSetWeight(scribbleId: scribbleId, exerciseId: exerciseId, setId: setId, newWeight: 120.5)

        let updatedSet = mockRepo.updatedScribbles[0]
            .exercises.first { $0.id == exerciseId }?
            .sets.first { $0.id == setId }
        XCTAssertEqual(updatedSet?.weight, 120.5)
    }

    func test_updateSetWeight_notFound_returnsWithoutUpdate() async throws {
        mockRepo.scribbleToReturn = nil
        try await sut.updateSetWeight(scribbleId: scribbleId, exerciseId: exerciseId, setId: setId, newWeight: 50)
        XCTAssertTrue(mockRepo.updatedScribbles.isEmpty)
    }

    // MARK: updateSetReps

    func test_updateSetReps_updatesCorrectSet() async throws {
        mockRepo.scribbleToReturn = makeFullScribble()

        try await sut.updateSetReps(scribbleId: scribbleId, exerciseId: exerciseId, setId: setId, newReps: 15)

        let updatedSet = mockRepo.updatedScribbles[0]
            .exercises.first { $0.id == exerciseId }?
            .sets.first { $0.id == setId }
        XCTAssertEqual(updatedSet?.reps, 15)
    }

    // MARK: deleteSet

    func test_deleteSet_removesSetAndReindexes() async throws {
        let set2Id = UUID()
        let set1 = ExerciseSet(id: setId, setNumber: 1, weight: 100, reps: 10)
        let set2 = ExerciseSet(id: set2Id, setNumber: 2, weight: 90, reps: 8)
        let exercise = Exercise(id: exerciseId, canonicalName: "Bench", muscleGroup: "Chest", sets: [set1, set2])
        let scribble = Scribble(id: scribbleId, rawText: "bench", status: .success, exercises: [exercise])
        mockRepo.scribbleToReturn = scribble

        try await sut.deleteSet(scribbleId: scribbleId, exerciseId: exerciseId, setId: setId)

        let remainingSets = mockRepo.updatedScribbles[0].exercises.first!.sets
        XCTAssertEqual(remainingSets.count, 1)
        XCTAssertEqual(remainingSets[0].setNumber, 1)  // Re-indexed
        XCTAssertEqual(remainingSets[0].id, set2Id)
    }

    // MARK: deleteExercise

    func test_deleteExercise_removesExercise() async throws {
        let ex1 = Exercise(id: exerciseId, canonicalName: "Squat", muscleGroup: "Legs")
        let ex2 = Exercise(id: UUID(), canonicalName: "Bench", muscleGroup: "Chest")
        let scribble = Scribble(id: scribbleId, rawText: "test", status: .success, exercises: [ex1, ex2])
        mockRepo.scribbleToReturn = scribble

        try await sut.deleteExercise(scribbleId: scribbleId, exerciseId: exerciseId)

        XCTAssertFalse(mockRepo.updatedScribbles.isEmpty)
        XCTAssertFalse(mockRepo.updatedScribbles[0].exercises.contains { $0.id == exerciseId })
    }

    func test_deleteExercise_lastExercise_deletesScribble() async throws {
        let ex = Exercise(id: exerciseId, canonicalName: "Squat", muscleGroup: "Legs")
        let scribble = Scribble(id: scribbleId, rawText: "test", status: .success, exercises: [ex])
        mockRepo.scribbleToReturn = scribble

        try await sut.deleteExercise(scribbleId: scribbleId, exerciseId: exerciseId)

        XCTAssertTrue(mockRepo.deletedIds.contains(scribbleId))
    }

    // MARK: addSet

    func test_addSet_appendsNewSetWithNextNumber() async throws {
        mockRepo.scribbleToReturn = makeFullScribble()

        try await sut.addSet(scribbleId: scribbleId, exerciseId: exerciseId)

        let sets = mockRepo.updatedScribbles[0].exercises.first!.sets
        XCTAssertEqual(sets.count, 2)
        XCTAssertEqual(sets[1].setNumber, 2)
    }

    func test_addSet_notFound_returnsWithoutUpdate() async throws {
        mockRepo.scribbleToReturn = nil
        try await sut.addSet(scribbleId: scribbleId, exerciseId: exerciseId)
        XCTAssertTrue(mockRepo.updatedScribbles.isEmpty)
    }
}
