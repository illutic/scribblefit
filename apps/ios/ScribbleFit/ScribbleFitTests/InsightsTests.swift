import XCTest
import Combine
@testable import CoreModel
@testable import FeatureAI
@testable import FeatureWorkouts
@testable import FeatureInsights

// MARK: - Test Helpers

@MainActor
private final class InsightsTestWorkoutRepository: WorkoutRepository {
    var workoutsByDate: [Date: [Workout]] = [:]
    var savedWorkouts: [Workout] = []

    func getWorkout(id: UUID) async throws -> Workout? { nil }

    func saveWorkout(_ workout: Workout) async throws {
        savedWorkouts.append(workout)
    }

    func deleteWorkout(id: UUID) async throws {}

    func getWorkouts(for date: Date) -> AsyncStream<[Workout]> {
        let calendar = Calendar.current
        let dayStart = calendar.startOfDay(for: date)
        let result = workoutsByDate[dayStart] ?? []
        return AsyncStream { continuation in
            continuation.yield(result)
            continuation.finish()
        }
    }

    func getWorkoutsInRange(startDate: Date, endDate: Date) -> AsyncStream<[Workout]> {
        let calendar = Calendar.current
        var allWorkouts: [Workout] = []
        var currentDate = calendar.startOfDay(for: startDate)
        let end = calendar.startOfDay(for: endDate)

        while currentDate <= end {
            let dayWorkouts = workoutsByDate[currentDate] ?? []
            allWorkouts.append(contentsOf: dayWorkouts)
            guard let nextDate = calendar.date(byAdding: .day, value: 1, to: currentDate) else { break }
            currentDate = nextDate
        }
        
        return AsyncStream { continuation in
            continuation.yield(allWorkouts)
            continuation.finish()
        }
    }

    func addWorkout(on date: Date, exercises: [Exercise]) {
        let calendar = Calendar.current
        let dayStart = calendar.startOfDay(for: date)
        let workout = Workout(date: date, exercises: exercises)
        workoutsByDate[dayStart, default: []].append(workout)
    }
}

private func makeExercise(
    name: String = "Bench Press",
    muscleGroup: String = "Chest",
    sets: [ExerciseSet]
) -> Exercise {
    Exercise(
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

// MARK: - GetVolumeInsightsUseCase Tests

extension AsyncStream {
    fileprivate func first() async -> Element? {
        var iterator = makeAsyncIterator()
        return await iterator.next()
    }
}

final class GetVolumeInsightsUseCaseTests: XCTestCase {

    @MainActor
    func testVolumeCalculation_singleDay() async throws {
        let repo = InsightsTestWorkoutRepository()
        let today = Calendar.current.startOfDay(for: Date())

        let exercise = makeExercise(sets: [
            makeSet(weight: 100, reps: 10, setNumber: 1),
            makeSet(weight: 100, reps: 8, setNumber: 2)
        ])
        repo.addWorkout(on: today, exercises: [exercise])

        let useCase = GetVolumeInsightsUseCase(workoutRepository: repo)
        let result = await useCase.execute(startDate: today, endDate: today).first() ?? []

        XCTAssertEqual(result.count, 1)
        // Volume = (100 * 10) + (100 * 8) = 1800
        XCTAssertEqual(result[0].volume, 1800.0, accuracy: 0.01)
    }

    @MainActor
    func testVolumeCalculation_multipleDays() async throws {
        let repo = InsightsTestWorkoutRepository()
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())
        let yesterday = calendar.date(byAdding: .day, value: -1, to: today)!

        repo.addWorkout(on: today, exercises: [
            makeExercise(sets: [makeSet(weight: 100, reps: 10)])
        ])
        repo.addWorkout(on: yesterday, exercises: [
            makeExercise(sets: [makeSet(weight: 50, reps: 20)])
        ])

        let useCase = GetVolumeInsightsUseCase(workoutRepository: repo)
        let result = await useCase.execute(startDate: yesterday, endDate: today).first() ?? []

        XCTAssertEqual(result.count, 2)

        let sortedResult = result.sorted { $0.date < $1.date }
        XCTAssertEqual(sortedResult[0].volume, 1000.0, accuracy: 0.01) // 50 * 20
        XCTAssertEqual(sortedResult[1].volume, 1000.0, accuracy: 0.01) // 100 * 10
    }

    @MainActor
    func testVolumeCalculation_noWorkouts() async throws {
        let repo = InsightsTestWorkoutRepository()
        let today = Calendar.current.startOfDay(for: Date())

        let useCase = GetVolumeInsightsUseCase(workoutRepository: repo)
        let result = await useCase.execute(startDate: today, endDate: today).first() ?? []

        XCTAssertTrue(result.isEmpty)
    }
}

// MARK: - GetFrequencyInsightsUseCase Tests

final class GetFrequencyInsightsUseCaseTests: XCTestCase {

    @MainActor
    func testFrequency_withWorkouts() async throws {
        let repo = InsightsTestWorkoutRepository()
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())
        let sevenDaysAgo = calendar.date(byAdding: .day, value: -6, to: today)!

        // Add 3 workouts across the week
        repo.addWorkout(on: today, exercises: [
            makeExercise(sets: [makeSet()])
        ])
        repo.addWorkout(on: calendar.date(byAdding: .day, value: -2, to: today)!, exercises: [
            makeExercise(sets: [makeSet()])
        ])
        repo.addWorkout(on: calendar.date(byAdding: .day, value: -4, to: today)!, exercises: [
            makeExercise(sets: [makeSet()])
        ])

        let useCase = GetFrequencyInsightsUseCase(workoutRepository: repo)
        let result = await useCase.execute(startDate: sevenDaysAgo, endDate: today).first()!

        XCTAssertEqual(result.totalWorkouts, 3)
        // 6 days / 7 = ~0.857 weeks, 3 / 0.857 = ~3.5 workouts/week
        XCTAssertGreaterThan(result.workoutsPerWeek, 2.0)
    }

    @MainActor
    func testFrequency_noWorkouts() async throws {
        let repo = InsightsTestWorkoutRepository()
        let today = Calendar.current.startOfDay(for: Date())

        let useCase = GetFrequencyInsightsUseCase(workoutRepository: repo)
        let result = await useCase.execute(startDate: today, endDate: today).first()!

        XCTAssertEqual(result.totalWorkouts, 0)
        XCTAssertEqual(result.workoutsPerWeek, 0.0, accuracy: 0.01)
    }
}

// MARK: - GetMuscleDistributionInsightsUseCase Tests

final class GetMuscleDistributionInsightsUseCaseTests: XCTestCase {

    @MainActor
    func testDistribution_multipleMuscleGroups() async throws {
        let repo = InsightsTestWorkoutRepository()
        let today = Calendar.current.startOfDay(for: Date())

        // 3 chest sets, 2 back sets = 60%, 40%
        repo.addWorkout(on: today, exercises: [
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

        let useCase = GetMuscleDistributionInsightsUseCase(workoutRepository: repo)
        let result = await useCase.execute(startDate: today, endDate: today).first() ?? []

        XCTAssertEqual(result.count, 2)

        // Sorted by percentage descending
        XCTAssertEqual(result[0].muscleGroup, "Chest")
        XCTAssertEqual(result[0].percentage, 60.0, accuracy: 0.01)
        XCTAssertEqual(result[1].muscleGroup, "Back")
        XCTAssertEqual(result[1].percentage, 40.0, accuracy: 0.01)
    }

    @MainActor
    func testDistribution_noWorkouts() async throws {
        let repo = InsightsTestWorkoutRepository()
        let today = Calendar.current.startOfDay(for: Date())

        let useCase = GetMuscleDistributionInsightsUseCase(workoutRepository: repo)
        let result = await useCase.execute(startDate: today, endDate: today).first() ?? []

        XCTAssertTrue(result.isEmpty)
    }

    @MainActor
    func testDistribution_singleMuscleGroup() async throws {
        let repo = InsightsTestWorkoutRepository()
        let today = Calendar.current.startOfDay(for: Date())

        repo.addWorkout(on: today, exercises: [
            makeExercise(name: "Bench Press", muscleGroup: "Chest", sets: [
                makeSet(setNumber: 1),
                makeSet(setNumber: 2)
            ])
        ])

        let useCase = GetMuscleDistributionInsightsUseCase(workoutRepository: repo)
        let result = await useCase.execute(startDate: today, endDate: today).first() ?? []

        XCTAssertEqual(result.count, 1)
        XCTAssertEqual(result[0].muscleGroup, "Chest")
        XCTAssertEqual(result[0].percentage, 100.0, accuracy: 0.01)
    }
}

// MARK: - InsightsState Tests

final class InsightsStateTests: XCTestCase {

    func testIsEmpty_whenLoading() {
        var state = InsightsState()
        state.isLoading = true
        state.frequency = nil

        // isEmpty is false during loading
        XCTAssertFalse(state.isEmpty)
    }

    func testIsEmpty_whenNoFrequencyData() {
        var state = InsightsState()
        state.isLoading = false
        state.frequency = nil

        XCTAssertTrue(state.isEmpty)
    }

    func testIsEmpty_whenTooFewWorkouts() {
        var state = InsightsState()
        state.isLoading = false
        state.frequency = FrequencyData(totalWorkouts: 1, workoutsPerWeek: 1.0)

        XCTAssertTrue(state.isEmpty)
    }

    func testIsNotEmpty_whenEnoughWorkouts() {
        var state = InsightsState()
        state.isLoading = false
        state.frequency = FrequencyData(totalWorkouts: 3, workoutsPerWeek: 3.0)

        XCTAssertFalse(state.isEmpty)
    }

    func testTotalVolume_calculation() {
        var state = InsightsState()
        state.volumePoints = [
            VolumeDataPoint(date: Date(), volume: 1000),
            VolumeDataPoint(date: Date(), volume: 2000)
        ]

        XCTAssertEqual(state.totalVolume, 3000.0, accuracy: 0.01)
    }

    func testTotalExercises_matchesFrequencyCount() {
        var state = InsightsState()
        state.frequency = FrequencyData(totalWorkouts: 3, workoutsPerWeek: 3.0, totalExercises: 7)

        XCTAssertEqual(state.totalExercises, 7)
    }
}
