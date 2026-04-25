import XCTest
import Combine
@testable import CoreModel
@testable import FeatureAI
@testable import FeatureInsights

// MARK: - Test Helpers

@MainActor
private final class InsightsTestScribbleRepository: ScribbleRepository {
    var savedScribbles: [Scribble] = []

    func getScribble(id: UUID) async throws -> Scribble? {
        savedScribbles.first { $0.id == id }
    }

    func addScribble(_ scribble: Scribble) async throws {
        savedScribbles.append(scribble)
    }

    func updateScribble(_ scribble: Scribble) async throws {
        if let index = savedScribbles.firstIndex(where: { $0.id == scribble.id }) {
            savedScribbles[index] = scribble
        }
    }

    func confirmScribble(_ scribble: Scribble) async throws {
        var completed = scribble
        completed.status = .completed
        try await updateScribble(completed)
    }

    func deleteScribble(id: UUID) async throws {
        savedScribbles.removeAll { $0.id == id }
    }

    func observeScribbles(for date: Date) -> AsyncStream<[Scribble]> {
        let calendar = Calendar.current
        let dayStart = calendar.startOfDay(for: date)
        let result = savedScribbles.filter { calendar.isDate($0.createdAt, inSameDayAs: dayStart) }
        return AsyncStream { continuation in
            continuation.yield(result)
            continuation.finish()
        }
    }

    func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> {
        let result = savedScribbles.filter { scribble in
            let date = scribble.createdAt
            return date >= startDate && date <= endDate
        }
        
        return AsyncStream { continuation in
            continuation.yield(result)
            continuation.finish()
        }
    }

    func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]> {
        let result = savedScribbles.filter { scribble in
            scribble.exercises.contains { $0.canonicalName.lowercased() == exerciseName.lowercased() }
        }
        return AsyncStream { continuation in
            continuation.yield(result)
            continuation.finish()
        }
    }

    func clearScribbleExercises(scribbleId: UUID) async throws {
        if let index = savedScribbles.firstIndex(where: { $0.id == scribbleId }) {
            savedScribbles[index].exercises = []
        }
    }

    func addScribble(on date: Date, exercises: [Exercise], status: ScribbleStatus = .completed) {
        let scribble = Scribble(
            id: UUID(),
            rawText: "Test",
            status: status,
            createdAt: date,
            exercises: exercises
        )
        savedScribbles.append(scribble)
    }
}

private func makeExercise(
    name: String = "Bench Press",
    muscleGroup: String = "Chest",
    sets: [ExerciseSet]
) -> Exercise {
    Exercise(
        id: UUID(),
        canonicalName: name,
        muscleGroup: muscleGroup,
        sets: sets
    )
}

private func makeSet(
    weight: Float = 100.0,
    reps: Int = 10,
    setNumber: Int = 1
) -> ExerciseSet {
    ExerciseSet(id: UUID(), setNumber: setNumber, weight: weight, reps: reps)
}

// MARK: - AsyncStream helper

@MainActor
func firstItem<T>(from stream: AsyncStream<T>) async -> T? {
    for await item in stream {
        return item
    }
    return nil
}

// MARK: - GetVolumeInsightsUseCase Tests

final class GetVolumeInsightsUseCaseTests: XCTestCase {

    @MainActor
    func testVolumeCalculation_singleDay() async throws {
        let repo = InsightsTestScribbleRepository()
        let today = Calendar.current.startOfDay(for: Date())

        let exercise = makeExercise(sets: [
            makeSet(weight: 100, reps: 10, setNumber: 1),
            makeSet(weight: 100, reps: 8, setNumber: 2)
        ])
        repo.addScribble(on: today, exercises: [exercise])

        let useCase = GetVolumeInsightsUseCase(scribbleRepository: repo)
        let result = await firstItem(from: useCase.execute(startDate: today, endDate: today)) ?? []

        XCTAssertEqual(result.count, 1)
        // Volume = (100 * 10) + (100 * 8) = 1800
        XCTAssertEqual(result[0].volume, 1800.0, accuracy: 0.01)
    }

    @MainActor
    func testVolumeCalculation_multipleDays() async throws {
        let repo = InsightsTestScribbleRepository()
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())
        let yesterday = calendar.date(byAdding: .day, value: -1, to: today)!

        repo.addScribble(on: today, exercises: [
            makeExercise(sets: [makeSet(weight: 100, reps: 10)])
        ])
        repo.addScribble(on: yesterday, exercises: [
            makeExercise(sets: [makeSet(weight: 50, reps: 20)])
        ])

        let useCase = GetVolumeInsightsUseCase(scribbleRepository: repo)
        let result = await firstItem(from: useCase.execute(startDate: yesterday, endDate: today)) ?? []

        XCTAssertEqual(result.count, 2)

        let sortedResult = result.sorted { $0.date < $1.date }
        XCTAssertEqual(sortedResult[0].volume, 1000.0, accuracy: 0.01) // 50 * 20
        XCTAssertEqual(sortedResult[1].volume, 1000.0, accuracy: 0.01) // 100 * 10
    }

    @MainActor
    func testVolumeCalculation_noScribbles() async throws {
        let repo = InsightsTestScribbleRepository()
        let today = Calendar.current.startOfDay(for: Date())

        let useCase = GetVolumeInsightsUseCase(scribbleRepository: repo)
        let result = await firstItem(from: useCase.execute(startDate: today, endDate: today)) ?? []

        XCTAssertTrue(result.isEmpty)
    }
}

// MARK: - GetFrequencyInsightsUseCase Tests

final class GetFrequencyInsightsUseCaseTests: XCTestCase {

    @MainActor
    func testFrequency_withScribbles() async throws {
        let repo = InsightsTestScribbleRepository()
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())
        let sevenDaysAgo = calendar.date(byAdding: .day, value: -6, to: today)!

        // Add 3 sessions across the week
        repo.addScribble(on: today, exercises: [
            makeExercise(sets: [makeSet()])
        ])
        repo.addScribble(on: calendar.date(byAdding: .day, value: -2, to: today)!, exercises: [
            makeExercise(sets: [makeSet()])
        ])
        repo.addScribble(on: calendar.date(byAdding: .day, value: -4, to: today)!, exercises: [
            makeExercise(sets: [makeSet()])
        ])

        let useCase = GetFrequencyInsightsUseCase(scribbleRepository: repo)
        let result = await firstItem(from: useCase.execute(startDate: sevenDaysAgo, endDate: today))!

        XCTAssertEqual(result.totalWorkouts, 3)
        XCTAssertGreaterThan(result.workoutsPerWeek, 2.0)
    }
}

// MARK: - GetMuscleDistributionInsightsUseCase Tests

final class GetMuscleDistributionInsightsUseCaseTests: XCTestCase {

    @MainActor
    func testDistribution_multipleMuscleGroups() async throws {
        let repo = InsightsTestScribbleRepository()
        let today = Calendar.current.startOfDay(for: Date())

        // 3 chest sets, 2 back sets = 60%, 40%
        repo.addScribble(on: today, exercises: [
            makeExercise(name: "Bench Press", muscleGroup: "Chest", sets: [
                makeSet(setNumber: 1),
                makeSet(setNumber: 2),
                makeSet(setNumber: 3)
            ]),
            makeExercise(name: "Barbell Row", muscleGroup: "Back", sets: [
                makeSet(setNumber: 1),
                makeSet(setNumber: 2)
            ])
        ])

        let useCase = GetMuscleDistributionInsightsUseCase(scribbleRepository: repo)
        let result = await firstItem(from: useCase.execute(startDate: today, endDate: today)) ?? []

        XCTAssertEqual(result.count, 2)

        // Sorted by percentage descending
        XCTAssertEqual(result[0].muscleGroup, "Chest")
        XCTAssertEqual(result[0].percentage, 60.0, accuracy: 0.01)
        XCTAssertEqual(result[1].muscleGroup, "Back")
        XCTAssertEqual(result[1].percentage, 40.0, accuracy: 0.01)
    }
}

// MARK: - InsightsState Tests

final class InsightsStateTests: XCTestCase {

    func testIsEmpty_whenLoading() {
        var state = InsightsState()
        state.isLoading = true
        state.frequency = nil

        XCTAssertFalse(state.isEmpty)
    }

    func testIsEmpty_whenNoFrequencyData() {
        var state = InsightsState()
        state.isLoading = false
        state.frequency = nil

        XCTAssertTrue(state.isEmpty)
    }

    func testIsEmpty_whenTooFewSessions() {
        var state = InsightsState()
        state.isLoading = false
        state.frequency = FrequencyData(totalWorkouts: 1, workoutsPerWeek: 1.0)

        XCTAssertTrue(state.isEmpty)
    }

    func testIsNotEmpty_whenEnoughSessions() {
        var state = InsightsState()
        state.isLoading = false
        state.frequency = FrequencyData(totalWorkouts: 3, workoutsPerWeek: 3.0)

        XCTAssertFalse(state.isEmpty)
    }
}
