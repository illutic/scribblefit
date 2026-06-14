import Foundation
import CoreModel
import CoreCommon
import Observation

@Observable
@MainActor
public final class LedgerStore {
    public var state: LedgerState
    private let scribbleRepository: ScribbleRepository
    private var observationTask: Task<Void, Never>?

    public init(
        scribbleRepository: ScribbleRepository,
        initialState: LedgerState = LedgerState()
    ) {
        self.scribbleRepository = scribbleRepository
        self.state = initialState
        observeScribbles()
    }

    public func onIntent(_ intent: LedgerIntent) {
        switch intent {
        case .refresh:
            observeScribbles()
        case .updateDateRange(let startDate, let endDate):
            state = state.copy(startDate: startDate, endDate: endDate)
            observeScribbles()
        case .scribbleTapped(let id):
            state = state.copy(navigationState: .scribbleDetails(id))
        case .exerciseTapped(let name):
            state = state.copy(navigationState: .exerciseDetails(name))
        case .dismissDetails:
            state = state.copy(navigationState: .some(nil))
        }
    }

    private func observeScribbles() {
        observationTask?.cancel()
        observationTask = Task {
            let stream = scribbleRepository.observeScribbles(
                startDate: state.startDate,
                endDate: state.endDate
            )

            for await scribbles in stream {
                // Filter only completed scribbles
                let completed = scribbles.filter { $0.status == .completed }
                state = state.copy(scribbles: completed, isLoading: false)
            }
        }
    }
}
