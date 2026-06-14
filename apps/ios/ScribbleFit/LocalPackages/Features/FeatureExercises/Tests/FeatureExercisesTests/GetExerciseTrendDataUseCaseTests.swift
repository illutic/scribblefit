import XCTest
import CoreModel
import CoreCommon
@testable import FeatureExercises

// MARK: - Mock for Trend Tests

@MainActor
private final class MockTrendExerciseRepository: ExerciseRepository {
    var exercisesToStream: [Exercise] = []
    var exercisesToReturn: [Exercise] = []

    func getExercises(query: String) async throws -> [Exercise] { exercisesToReturn }
    func observeExercises(query: String) -> AsyncStream<[Exercise]> {
        let data = exercisesToStream
        return AsyncStream { cont in cont.yield(data); cont.finish() }
    }
    func getExercise(id: UUID) async throws -> Exercise? { nil }
    func saveExercise(_ exercise: Exercise) async throws {}
    func saveExercise(_ exercise: Exercise, to scribbleId: UUID) async throws {}
    func updateExercise(_ exercise: Exercise) async throws {}
    func deleteExercise(id: UUID) async throws {}
}

private func makeTrendExercise(
    name: String = "Bench Press",
    sets: [ExerciseSet],
    daysAgo: Int = 0
) -> Exercise {
    Exercise(
        id: UUID(),
        canonicalName: name,
        muscleGroup: "Chest",
        sets: sets,
        createdAt: Date(timeIntervalSinceNow: -Double(daysAgo) * 86400)
    )
}

private func makeTrendSet(weight: Float, reps: Int) -> ExerciseSet {
    ExerciseSet(id: UUID(), setNumber: 1, weight: weight, reps: reps)
}

// MARK: - GetExerciseTrendDataUseCase Tests

@MainActor
final class GetExerciseTrendDataUseCaseTests: XCTestCase {

    private var mockRepo: MockTrendExerciseRepository!
    private var sut: GetExerciseTrendDataUseCase!

    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockTrendExerciseRepository()
        sut = GetExerciseTrendDataUseCase(exerciseRepository: mockRepo)
    }

    // MARK: - Empty History

    func test_execute_emptyHistory_yieldsEmptyResult() async {
        mockRepo.exercisesToStream = []

        let stream = sut.execute(exerciseName: "Bench", period: .all)
        var result: ExerciseTrendResult?
        for await value in stream { result = value; break }

        XCTAssertEqual(result?.oneRM.dataPoints.count, 0)
        XCTAssertEqual(result?.volume.dataPoints.count, 0)
    }

    func test_execute_emptyHistory_trendDirectionIsStable() async {
        mockRepo.exercisesToStream = []

        let stream = sut.execute(exerciseName: "Bench", period: .all)
        var result: ExerciseTrendResult?
        for await value in stream { result = value; break }

        XCTAssertEqual(result?.oneRM.insights.trendDirection, .stable)
    }

    // MARK: - Happy Path

    func test_execute_withData_yieldsDataPoints() async {
        let ex1 = makeTrendExercise(sets: [makeTrendSet(weight: 100, reps: 5)], daysAgo: 10)
        let ex2 = makeTrendExercise(sets: [makeTrendSet(weight: 110, reps: 5)], daysAgo: 5)
        mockRepo.exercisesToStream = [ex1, ex2]

        let stream = sut.execute(exerciseName: "Bench Press", period: .all)
        var result: ExerciseTrendResult?
        for await value in stream { result = value; break }

        XCTAssertEqual(result?.oneRM.dataPoints.count, 2)
        XCTAssertEqual(result?.volume.dataPoints.count, 2)
    }

    func test_execute_improvingData_trendDirectionImproving() async {
        // Large improvement: 100kg -> 200kg = +100%
        let ex1 = makeTrendExercise(sets: [makeTrendSet(weight: 100, reps: 1)], daysAgo: 30)
        let ex2 = makeTrendExercise(sets: [makeTrendSet(weight: 200, reps: 1)], daysAgo: 1)
        mockRepo.exercisesToStream = [ex1, ex2]

        let stream = sut.execute(exerciseName: "Bench Press", period: .all)
        var result: ExerciseTrendResult?
        for await value in stream { result = value; break }

        XCTAssertEqual(result?.oneRM.insights.trendDirection, .improving)
    }

    func test_execute_decliningData_trendDirectionDeclining() async {
        // Significant decline: 200kg -> 100kg = -50%
        let ex1 = makeTrendExercise(sets: [makeTrendSet(weight: 200, reps: 1)], daysAgo: 30)
        let ex2 = makeTrendExercise(sets: [makeTrendSet(weight: 100, reps: 1)], daysAgo: 1)
        mockRepo.exercisesToStream = [ex1, ex2]

        let stream = sut.execute(exerciseName: "Bench Press", period: .all)
        var result: ExerciseTrendResult?
        for await value in stream { result = value; break }

        XCTAssertEqual(result?.oneRM.insights.trendDirection, .declining)
    }

    // MARK: - Period Filtering

    func test_execute_oneMonthPeriod_filtersOldData() async {
        // One exercise 2 months ago, one recent
        let old = makeTrendExercise(sets: [makeTrendSet(weight: 100, reps: 5)], daysAgo: 61)
        let recent = makeTrendExercise(sets: [makeTrendSet(weight: 110, reps: 5)], daysAgo: 1)
        mockRepo.exercisesToStream = [old, recent]

        let stream = sut.execute(exerciseName: "Bench Press", period: .oneMonth)
        var result: ExerciseTrendResult?
        for await value in stream { result = value; break }

        XCTAssertEqual(result?.oneRM.dataPoints.count, 1)
    }

    func test_execute_allPeriod_includesAllData() async {
        let exercises = (1...5).map { i in
            makeTrendExercise(sets: [makeTrendSet(weight: Float(i * 20), reps: 5)], daysAgo: i * 30)
        }
        mockRepo.exercisesToStream = exercises

        let stream = sut.execute(exerciseName: "Bench Press", period: .all)
        var result: ExerciseTrendResult?
        for await value in stream { result = value; break }

        XCTAssertEqual(result?.oneRM.dataPoints.count, 5)
    }

    // MARK: - Personal Best

    func test_execute_personalBest_isMaxAcrossAllPoints() async throws {
        let ex1 = makeTrendExercise(sets: [makeTrendSet(weight: 100, reps: 1)], daysAgo: 30)
        let ex2 = makeTrendExercise(sets: [makeTrendSet(weight: 150, reps: 1)], daysAgo: 1)
        mockRepo.exercisesToStream = [ex1, ex2]

        let stream = sut.execute(exerciseName: "Bench Press", period: .all)
        var result: ExerciseTrendResult?
        for await value in stream { result = value; break }

        let pb = try XCTUnwrap(result?.oneRM.insights.personalBest)
        XCTAssertEqual(pb, 150.0, accuracy: 0.01)
    }

    // MARK: - Volume Calculation

    func test_execute_volumeCalculatedCorrectly() async throws {
        // 100kg * 5 reps = 500 volume
        let ex = makeTrendExercise(sets: [makeTrendSet(weight: 100, reps: 5)], daysAgo: 1)
        mockRepo.exercisesToStream = [ex]

        let stream = sut.execute(exerciseName: "Bench Press", period: .all)
        var result: ExerciseTrendResult?
        for await value in stream { result = value; break }

        let value = try XCTUnwrap(result?.volume.dataPoints.first?.value)
        XCTAssertEqual(value, 500.0, accuracy: 0.01)
    }
}
