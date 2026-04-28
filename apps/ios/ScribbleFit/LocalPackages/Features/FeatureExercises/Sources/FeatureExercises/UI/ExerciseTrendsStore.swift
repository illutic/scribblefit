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
            loadData()
        case .updatePeriod(let period):
            state.selectedPeriod = period
            loadData()
        }
    }
    
    private func loadData() {
        state.isLoading = true
        Task {
            do {
                async let oneRMResult = getExerciseTrendDataUseCase.execute(
                    exerciseName: state.exerciseName,
                    metric: .oneRM,
                    period: state.selectedPeriod
                )
                
                async let volumeResult = getExerciseTrendDataUseCase.execute(
                    exerciseName: state.exerciseName,
                    metric: .volume,
                    period: state.selectedPeriod
                )
                
                let (oneRM, volume) = try await (oneRMResult, volumeResult)
                
                self.state.oneRMDataPoints = oneRM.dataPoints
                self.state.oneRMInsights = oneRM.insights
                
                self.state.volumeDataPoints = volume.dataPoints
                self.state.volumeInsights = volume.insights
                
                self.state.error = nil
            } catch {
                self.state.error = error.localizedDescription
            }
            self.state.isLoading = false
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
