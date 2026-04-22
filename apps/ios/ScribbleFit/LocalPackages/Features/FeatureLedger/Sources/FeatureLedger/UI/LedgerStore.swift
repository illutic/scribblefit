import SwiftUI
import Observation
import CoreModel

@Observable
@MainActor
public final class LedgerStore {
    public private(set) var state: LedgerState
    private let getWorkoutsInRangeUseCase: GetWorkoutsInRangeUseCase
    private let configRepository: ConfigRepository
    private var observationTask: Task<Void, Never>?
    private var configTask: Task<Void, Never>?

    public init(
        getWorkoutsInRangeUseCase: GetWorkoutsInRangeUseCase,
        configRepository: ConfigRepository,
        initialState: LedgerState = .init()
    ) {
        self.getWorkoutsInRangeUseCase = getWorkoutsInRangeUseCase
        self.configRepository = configRepository
        self.state = initialState
        observeWorkouts()
        observeConfig()
    }

    public func handleIntent(_ intent: LedgerIntent) {
        switch intent {
        case .refresh:
            observeWorkouts()
        case let .updateDateRange(startDate, endDate):
            state = state.copy(startDate: startDate, endDate: endDate)
            observeWorkouts()
        case .workoutTapped(let id):
            state = state.copy(navigationState: .workoutExercises(id))
        case .exerciseTapped(let name):
            state = state.copy(navigationState: .exerciseDetails(name))
        case .dismissDetails:
            state = state.copy(navigationState: .some(nil))
        }
    }

    private func observeWorkouts() {
        observationTask?.cancel()
        state = state.copy(isLoading: true)
        observationTask = Task {
            let stream = getWorkoutsInRangeUseCase.execute(
                startDate: state.startDate,
                endDate: state.endDate
            )
            for await workouts in stream {
                if Task.isCancelled { break }
                state = state.copy(workouts: workouts, isLoading: false)
            }
        }
    }

    private func observeConfig() {
        configTask?.cancel()
        configTask = Task {
            for await config in configRepository.configPublisher.values {
                if Task.isCancelled { break }
                state = state.copy(weightUnit: config.weightUnit)
            }
        }
    }
}
