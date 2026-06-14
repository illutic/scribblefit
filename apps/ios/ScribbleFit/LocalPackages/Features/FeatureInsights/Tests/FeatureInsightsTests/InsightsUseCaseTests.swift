import XCTest
import CoreModel
@testable import FeatureInsights

// MARK: - Mocks

@MainActor
private final class MockScribbleRepository: ScribbleRepository {
    var scribblesToStream: [Scribble] = []

    func observeScribbles(for date: Date) -> AsyncStream<[Scribble]> {
        let data = scribblesToStream
        return AsyncStream { cont in cont.yield(data); cont.finish() }
    }
    func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> {
        let data = scribblesToStream
        return AsyncStream { cont in cont.yield(data); cont.finish() }
    }
    func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]> {
        let data = scribblesToStream
        return AsyncStream { cont in cont.yield(data); cont.finish() }
    }
    func addScribble(_ scribble: Scribble) async throws {}
    func updateScribble(_ scribble: Scribble) async throws {}
    func deleteScribble(id: UUID) async throws {}
    func getScribble(id: UUID) async throws -> Scribble? { nil }
    func clearScribbleExercises(scribbleId: UUID) async throws {}
    func confirmScribble(_ scribble: Scribble) async throws {}
}

@MainActor
private final class MockLLMService: LLMService {
    var insightsToReturn: [AIInsight] = []
    var shouldThrow: Error? = nil

    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        ParsedWorkoutResult(exercises: [], rawText: rawText)
    }
    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        if let e = shouldThrow { throw e }
        return insightsToReturn
    }
    func generateExerciseInsight(history: String) async throws -> AIInsight {
        AIInsight(insightType: .trend, text: "stub")
    }
    func isSupported() async -> Bool { false }
}

// MARK: - Helpers

private func makeSet(weight: Float, reps: Int) -> ExerciseSet {
    ExerciseSet(id: UUID(), setNumber: 1, weight: weight, reps: reps)
}

private func makeExercise(
    name: String = "Bench Press",
    muscleGroup: String = "Chest",
    sets: [ExerciseSet] = []
) -> Exercise {
    Exercise(id: UUID(), canonicalName: name, muscleGroup: muscleGroup, sets: sets)
}

private func makeCompletedScribble(exercises: [Exercise] = [], date: Date = Date()) -> Scribble {
    Scribble(id: UUID(), rawText: "test", status: .completed, createdAt: date, exercises: exercises)
}

private func makePendingScribble() -> Scribble {
    Scribble(id: UUID(), rawText: "test", status: .pending)
}

// MARK: - GetFrequencyInsightsUseCase Tests

@MainActor
final class GetFrequencyInsightsUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var sut: GetFrequencyInsightsUseCase!

    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockScribbleRepository()
        sut = GetFrequencyInsightsUseCase(scribbleRepository: mockRepo)
    }

    func test_execute_countsOnlyCompletedScribbles() async {
        mockRepo.scribblesToStream = [
            makeCompletedScribble(),
            makeCompletedScribble(),
            makePendingScribble()
        ]
        let start = Date(timeIntervalSinceNow: -7 * 86400)
        let end = Date()

        let stream = sut.execute(startDate: start, endDate: end)
        var result: FrequencyData? = nil
        for await data in stream {
            result = data
            break
        }

        XCTAssertEqual(result?.totalWorkouts, 2)
    }

    func test_execute_countsTotalExercises() async {
        let ex1 = makeExercise()
        let ex2 = makeExercise()
        mockRepo.scribblesToStream = [makeCompletedScribble(exercises: [ex1, ex2])]
        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: FrequencyData? = nil
        for await data in stream { result = data; break }

        XCTAssertEqual(result?.totalExercises, 2)
    }

    func test_execute_emptyScribbles_returnsZeroes() async {
        mockRepo.scribblesToStream = []
        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: FrequencyData? = nil
        for await data in stream { result = data; break }

        XCTAssertEqual(result?.totalWorkouts, 0)
        XCTAssertEqual(result?.totalExercises, 0)
    }

    func test_execute_workoutsPerWeek_calculatedOverPeriod() async {
        // 2 workouts over 14 days = 1 per week
        let start = Date(timeIntervalSinceNow: -14 * 86400)
        let end = Date()
        mockRepo.scribblesToStream = [makeCompletedScribble(), makeCompletedScribble()]

        let stream = sut.execute(startDate: start, endDate: end)
        var result: FrequencyData? = nil
        for await data in stream { result = data; break }

        XCTAssertEqual(result?.workoutsPerWeek ?? 0, 1.0, accuracy: 0.1)
    }
}

// MARK: - GetMuscleDistributionInsightsUseCase Tests

@MainActor
final class GetMuscleDistributionInsightsUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var sut: GetMuscleDistributionInsightsUseCase!

    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockScribbleRepository()
        sut = GetMuscleDistributionInsightsUseCase(scribbleRepository: mockRepo)
    }

    func test_execute_emptyScribbles_returnsEmpty() async {
        mockRepo.scribblesToStream = []
        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [MuscleGroupDistribution] = []
        for await data in stream { result = data; break }
        XCTAssertTrue(result.isEmpty)
    }

    func test_execute_singleMuscleGroup_returns100Percent() async {
        let sets = [makeSet(weight: 100, reps: 10), makeSet(weight: 100, reps: 10)]
        let ex = makeExercise(muscleGroup: "Chest", sets: sets)
        mockRepo.scribblesToStream = [makeCompletedScribble(exercises: [ex])]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [MuscleGroupDistribution] = []
        for await data in stream { result = data; break }

        XCTAssertEqual(result.count, 1)
        XCTAssertEqual(result[0].muscleGroup, "Chest")
        XCTAssertEqual(result[0].percentage, 100.0, accuracy: 0.01)
    }

    func test_execute_twoMuscleGroupsEqualSets_each50Percent() async {
        let set = makeSet(weight: 100, reps: 10)
        let chestEx = makeExercise(muscleGroup: "Chest", sets: [set])
        let legEx = makeExercise(muscleGroup: "Legs", sets: [set])
        let scribble = makeCompletedScribble(exercises: [chestEx, legEx])
        mockRepo.scribblesToStream = [scribble]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [MuscleGroupDistribution] = []
        for await data in stream { result = data; break }

        XCTAssertEqual(result.count, 2)
        XCTAssertEqual(result[0].percentage, 50.0, accuracy: 0.01)
        XCTAssertEqual(result[1].percentage, 50.0, accuracy: 0.01)
    }

    func test_execute_pendingScribblesIgnored() async {
        let ex = makeExercise(muscleGroup: "Chest", sets: [makeSet(weight: 100, reps: 10)])
        mockRepo.scribblesToStream = [makePendingScribble(), makeCompletedScribble(exercises: [ex])]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [MuscleGroupDistribution] = []
        for await data in stream { result = data; break }

        // Only 1 group from completed scribble
        XCTAssertEqual(result.count, 1)
    }

    func test_execute_sortedDescendingByPercentage() async {
        let singleSet = makeSet(weight: 100, reps: 10)
        let doubleSets = [makeSet(weight: 100, reps: 10), makeSet(weight: 100, reps: 10)]
        let chestEx = makeExercise(muscleGroup: "Chest", sets: doubleSets)
        let legEx = makeExercise(muscleGroup: "Legs", sets: [singleSet])
        mockRepo.scribblesToStream = [makeCompletedScribble(exercises: [chestEx, legEx])]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [MuscleGroupDistribution] = []
        for await data in stream { result = data; break }

        XCTAssertEqual(result.first?.muscleGroup, "Chest")  // More sets = higher %
    }
}

// MARK: - GetVolumeInsightsUseCase Tests

@MainActor
final class GetVolumeInsightsUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var sut: GetVolumeInsightsUseCase!

    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockScribbleRepository()
        sut = GetVolumeInsightsUseCase(scribbleRepository: mockRepo)
    }

    func test_execute_emptyScribbles_returnsEmpty() async {
        mockRepo.scribblesToStream = []
        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [VolumeDataPoint] = []
        for await data in stream { result = data; break }
        XCTAssertTrue(result.isEmpty)
    }

    func test_execute_calculatesVolumeCorrectly() async {
        // 100kg * 10 reps = 1000 volume
        let sets = [makeSet(weight: 100, reps: 10)]
        let ex = makeExercise(sets: sets)
        let date = Date(timeIntervalSince1970: 1_000_000)
        mockRepo.scribblesToStream = [makeCompletedScribble(exercises: [ex], date: date)]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [VolumeDataPoint] = []
        for await data in stream { result = data; break }

        XCTAssertEqual(result.count, 1)
        XCTAssertEqual(result[0].volume, 1000.0, accuracy: 0.01)
    }

    func test_execute_zeroVolumeScribbles_filtered() async {
        let ex = makeExercise(sets: [makeSet(weight: 0, reps: 0)])
        mockRepo.scribblesToStream = [makeCompletedScribble(exercises: [ex])]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [VolumeDataPoint] = []
        for await data in stream { result = data; break }

        XCTAssertTrue(result.isEmpty)
    }

    func test_execute_pendingScribbles_notIncluded() async {
        let ex = makeExercise(sets: [makeSet(weight: 100, reps: 10)])
        mockRepo.scribblesToStream = [makePendingScribble()]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [VolumeDataPoint] = []
        for await data in stream { result = data; break }

        XCTAssertTrue(result.isEmpty)
    }
}

// MARK: - GetAIOverviewUseCase Tests

@MainActor
final class GetAIOverviewUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var mockLLM: MockLLMService!
    private var sut: GetAIOverviewUseCase!

    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockScribbleRepository()
        mockLLM = MockLLMService()
        sut = GetAIOverviewUseCase(scribbleRepository: mockRepo, llmProvider: mockLLM)
    }

    func test_execute_noExercises_returnsDefaultInsight() async throws {
        mockRepo.scribblesToStream = []

        let insights = try await sut.execute(date: Date())

        XCTAssertEqual(insights.count, 1)
        XCTAssertTrue(insights[0].text.contains("scribbling"))
    }

    func test_execute_withExercises_callsLLMAndReturnsInsights() async throws {
        let ex = makeExercise()
        mockRepo.scribblesToStream = [makeCompletedScribble(exercises: [ex])]
        mockLLM.insightsToReturn = [AIInsight(insightType: .summary, text: "Great job!")]

        let insights = try await sut.execute(date: Date())

        XCTAssertEqual(insights.count, 1)
        XCTAssertEqual(insights[0].text, "Great job!")
    }

    func test_execute_pendingScribblesIgnored_returnsDefault() async throws {
        let ex = makeExercise()
        mockRepo.scribblesToStream = [makePendingScribble()]

        let insights = try await sut.execute(date: Date())

        // Pending scribble has no exercises after filtering for .completed
        XCTAssertEqual(insights.count, 1)
        XCTAssertTrue(insights[0].text.contains("scribbling"))
    }

    func test_execute_llmError_propagates() async {
        let ex = makeExercise()
        mockRepo.scribblesToStream = [makeCompletedScribble(exercises: [ex])]
        mockLLM.shouldThrow = NSError(domain: "LLM", code: 500)

        do {
            _ = try await sut.execute(date: Date())
            XCTFail("Expected error")
        } catch {
            XCTAssertNotNil(error)
        }
    }
}
