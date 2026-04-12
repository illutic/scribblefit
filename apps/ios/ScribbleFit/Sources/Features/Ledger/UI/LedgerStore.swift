import SwiftUI
import Observation
#if SWIFT_PACKAGE
import CoreModel
#endif

@Observable
@MainActor
public final class LedgerStore {
    public private(set) var state: LedgerState
    private let getWorkoutsInRangeUseCase: GetWorkoutsInRangeUseCase

    public init(
        getWorkoutsInRangeUseCase: GetWorkoutsInRangeUseCase,
        initialState: LedgerState = .init()
    ) {
        self.getWorkoutsInRangeUseCase = getWorkoutsInRangeUseCase
        self.state = initialState
    }

    public func handleIntent(_ intent: LedgerIntent) {
        switch intent {
        case .refresh:
            fetchWorkouts()
        case let .updateDateRange(startDate, endDate):
            state.startDate = startDate
            state.endDate = endDate
            fetchWorkouts()
        case let .workoutTapped(id):
            // Navigation handled by View via ID
            break
        }
    }

    private func fetchWorkouts() {
        state.isLoading = true
        Task {
            do {
                let workouts = try await getWorkoutsInRangeUseCase.execute(
                    startDate: state.startDate,
                    endDate: state.endDate
                )
                state.workouts = workouts
                state.isLoading = false
            } catch {
                state.isLoading = false
                // handle error?
            }
        }
    }
}
