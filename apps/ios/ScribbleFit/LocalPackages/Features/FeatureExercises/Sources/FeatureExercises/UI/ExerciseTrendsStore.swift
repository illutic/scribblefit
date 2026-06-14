import Foundation
import Observation
import CoreModel
import Combine

@Observable
@MainActor
public final class ExerciseTrendsStore {
    public var state: ExerciseTrendsState

    private let getExerciseTrendDataUseCase: GetExerciseTrendDataUseCase
    private let configRepository: ConfigRepository

    private var cancellables = Set<AnyCancellable>()
    private var observationTask: Task<Void, Never>?

    public init(
        exerciseName: String,
        getExerciseTrendDataUseCase: GetExerciseTrendDataUseCase,
        configRepository: ConfigRepository
    ) {
        self.state = ExerciseTrendsState(exerciseName: exerciseName)
        self.getExerciseTrendDataUseCase = getExerciseTrendDataUseCase
        self.configRepository = configRepository

        setupWeightUnitObservation()
    }

    public func onIntent(_ intent: ExerciseTrendsIntent) {
        switch intent {
        case .loadData:
            startObserving()
        case .updatePeriod(let period):
            state.selectedPeriod = period
            startObserving()
        }
    }

    private func startObserving() {
        state.isLoading = true

        observationTask?.cancel()
        observationTask = Task {
            let stream = getExerciseTrendDataUseCase.execute(
                exerciseName: state.exerciseName,
                period: state.selectedPeriod
            )
            for await result in stream {
                self.state.oneRMDataPoints = result.oneRM.dataPoints
                self.state.oneRMInsights = result.oneRM.insights
                self.state.volumeDataPoints = result.volume.dataPoints
                self.state.volumeInsights = result.volume.insights
                self.state.isLoading = false
            }
        }
    }

    private func setupWeightUnitObservation() {
        configRepository.configPublisher
            .receive(on: RunLoop.main)
            .sink { [weak self] config in
                self?.state.weightUnit = config.weightUnit
            }
            .store(in: &cancellables)
    }
}
