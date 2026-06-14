import Foundation
import CoreModel

@MainActor
@Observable
public final class ExerciseHistoryStore {
    public var state: ExerciseHistoryState

    private let getExerciseHistoryUseCase: GetExerciseHistoryUseCase
    private let configRepository: ConfigRepository

    public init(
        exerciseName: String,
        getExerciseHistoryUseCase: GetExerciseHistoryUseCase,
        configRepository: ConfigRepository
    ) {
        self.state = ExerciseHistoryState(exerciseName: exerciseName)
        self.getExerciseHistoryUseCase = getExerciseHistoryUseCase
        self.configRepository = configRepository

        Task {
            await self.onIntent(.loadHistory)
        }
    }

    public func onIntent(_ intent: ExerciseHistoryIntent) {
        switch intent {
        case .loadHistory:
            Task {
                await loadHistory()
            }
        case .navigateToScribble(let _, let date):
            NotificationCenter.default.post(name: NSNotification.Name("NavigateToCanvasDate"), object: date)
        }
    }

    private func loadHistory() async {
        state.isLoading = true
        state.error = nil

        do {
            let weightUnit = configRepository.getConfig().weightUnit
            let history = try await getExerciseHistoryUseCase.execute(
                exerciseName: state.exerciseName,
                weightUnit: weightUnit
            )
            state.history = history
        } catch {
            state.error = error.localizedDescription
        }

        state.isLoading = false
    }
}
