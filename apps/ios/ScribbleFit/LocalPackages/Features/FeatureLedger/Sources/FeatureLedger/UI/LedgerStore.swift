import SwiftUI
import Observation
import CoreModel

@Observable
@MainActor
public final class LedgerStore {
    public private(set) var state: LedgerState
    private let getWorkoutsInRangeUseCase: GetWorkoutsInRangeUseCase
    private var observationTask: Task<Void, Never>?

    public init(
        getWorkoutsInRangeUseCase: GetWorkoutsInRangeUseCase,
        initialState: LedgerState = .init()
    ) {
        self.getWorkoutsInRangeUseCase = getWorkoutsInRangeUseCase
        self.state = initialState
        observeWorkouts()
    }

    public func handleIntent(_ intent: LedgerIntent) {
        switch intent {
        case .refresh:
            observeWorkouts()
        case let .updateDateRange(startDate, endDate):
            state.startDate = startDate
            state.endDate = endDate
            observeWorkouts()
        case .workoutTapped:
            // Navigation handled by View via ID
            break
        }
    }

    private func observeWorkouts() {
        observationTask?.cancel()
        state.isLoading = true
        observationTask = Task {
            let stream = getWorkoutsInRangeUseCase.execute(
                startDate: state.startDate,
                endDate: state.endDate
            )
            for await workouts in stream {
                if Task.isCancelled { break }
                state.workouts = workouts
                state.isLoading = false
            }
        }
    }
}
