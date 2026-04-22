import Foundation
import Observation
import CoreModel
import Combine

@Observable
@MainActor
public final class ExerciseDetailsStore {
    public var state: ExerciseDetailsState
    
    private let getExerciseDetailsUseCase: GetExerciseDetailsUseCase
    private let getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase
    private let configRepository: ConfigRepository
    
    private var cancellables = Set<AnyCancellable>()
    private var observationTask: Task<Void, Never>?
    
    public init(
        exerciseName: String,
        getExerciseDetailsUseCase: GetExerciseDetailsUseCase,
        getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase,
        configRepository: ConfigRepository
    ) {
        self.state = ExerciseDetailsState(exerciseName: exerciseName)
        self.getExerciseDetailsUseCase = getExerciseDetailsUseCase
        self.getExerciseAIInsightUseCase = getExerciseAIInsightUseCase
        self.configRepository = configRepository
        
        setupWeightUnitObservation()
        observeDetails()
    }
    
    public func onIntent(_ intent: ExerciseDetailsIntent) {
        switch intent {
        case .refreshAIInsight:
            refreshAIInsight()
        }
    }
    
    private func observeDetails() {
        observationTask?.cancel()
        observationTask = Task {
            let stream = getExerciseDetailsUseCase.execute(exerciseName: state.exerciseName)
            for await details in stream {
                if Task.isCancelled { break }
                self.state.details = details
                
                // Auto-trigger AI if not present
                if !details.history.isEmpty && state.aiInsight == nil {
                    refreshAIInsight()
                }
            }
        }
    }
    
    private func refreshAIInsight() {
        guard let history = state.details?.history, !history.isEmpty else { return }
        
        Task {
            state.isGeneratingAI = true
            do {
                let insight = try await getExerciseAIInsightUseCase.execute(history: history)
                state.aiInsight = insight
            } catch {
                state.error = error.localizedDescription
            }
            state.isGeneratingAI = false
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

public enum ExerciseDetailsIntent {
    case refreshAIInsight
}
