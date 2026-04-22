import Foundation
import Observation
import CoreModel
import Combine
import FeatureWorkouts

@Observable
@MainActor
public final class WorkoutExercisesStore {
    public var state: WorkoutExercisesState
    
    private let workoutRepository: WorkoutRepository
    private let configRepository: ConfigRepository
    private let calculateWorkoutVolumeUseCase: CalculateWorkoutVolumeUseCase
    private let formatWorkoutSummaryUseCase: FormatWorkoutSummaryUseCase
    private let formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase
    
    private var cancellables = Set<AnyCancellable>()
    private var observationTask: Task<Void, Never>?
    private var currentWorkout: Workout?
    
    public init(
        workoutId: UUID,
        workoutRepository: WorkoutRepository,
        configRepository: ConfigRepository,
        calculateWorkoutVolumeUseCase: CalculateWorkoutVolumeUseCase,
        formatWorkoutSummaryUseCase: FormatWorkoutSummaryUseCase,
        formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase
    ) {
        self.state = WorkoutExercisesState(workoutId: workoutId)
        self.workoutRepository = workoutRepository
        self.configRepository = configRepository
        self.calculateWorkoutVolumeUseCase = calculateWorkoutVolumeUseCase
        self.formatWorkoutSummaryUseCase = formatWorkoutSummaryUseCase
        self.formatExerciseSummaryUseCase = formatExerciseSummaryUseCase
        
        setupConfigObservation()
        observeWorkout()
    }
    
    public func onIntent(_ intent: WorkoutExercisesIntent) {
        switch intent {
        case .refresh:
            observeWorkout()
        case .exerciseClicked, .navigateBack:
            // Handled by view for now or route through intent later
            break
        }
    }
    
    private func observeWorkout() {
        observationTask?.cancel()
        state = state.copy(isLoading: true)
        
        observationTask = Task {
            let stream = workoutRepository.observeWorkout(id: state.workoutId)
            
            for await workout in stream {
                if Task.isCancelled { break }
                
                if let workout = workout {
                    self.currentWorkout = workout
                    self.refreshUiModels()
                    self.state = state.copy(isLoading: false)
                }
            }
        }
    }
    
    private func refreshUiModels() {
        guard let workout = currentWorkout else { return }

        let volumeValue = calculateWorkoutVolumeUseCase.execute(workout: workout)
        let volumeSummary = formatWorkoutSummaryUseCase.execute(totalVolume: volumeValue)
        let unitLabel = state.weightUnit == .kgs ? "kg" : "lbs"
        let totalVolumeDisplay = "\(volumeSummary.value)\(volumeSummary.isKilo ? "k" : "") \(unitLabel)"

        let uiModels = workout.exercises.map { exercise in
            WorkoutExerciseUiModel(
                id: exercise.id,
                name: exercise.canonicalName,
                formattedSummary: formatExerciseSummaryUseCase.execute(exercise: exercise, weightUnit: state.weightUnit),
                estimated1RMValue: exercise.estimated1RM.map { Int($0) },
                intensityValue: exercise.intensity.map { Int($0 * 100) },
                improvementValue: nil, // TODO - The exercise domain model has no improvement value. We need to implement it.
                hasStats: exercise.estimated1RM != nil || exercise.intensity != nil
            )
        }

        var newState = state
        newState.update(with: workout, uiModels: uiModels, totalVolume: totalVolumeDisplay)
        state = newState
    }
    
    private func setupConfigObservation() {
        state = state.copy(weightUnit: configRepository.getConfig().weightUnit)
        
        configRepository.configPublisher
            .receive(on: RunLoop.main)
            .sink { [weak self] config in
                guard let self = self else { return }
                self.state = state.copy(weightUnit: config.weightUnit)
                self.refreshUiModels()
            }
            .store(in: &cancellables)
    }
}
