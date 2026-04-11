import Foundation
import Observation
import Combine

@Observable
@MainActor
public final class CanvasStore {
    public var state = CanvasState()
    
    private let getScribblesForDateUseCase: GetScribblesForDateUseCase
    private let addRawScribbleUseCase: AddRawScribbleUseCase
    private let confirmScribbleUseCase: ConfirmScribbleUseCase
    private let deleteScribbleUseCase: DeleteScribbleUseCase
    private let parsePendingScribblesUseCase: ParsePendingScribblesUseCase
    private let getAIOverviewUseCase: GetAIOverviewUseCase
    private let configRepository: ConfigRepository
    
    private var observationTask: Task<Void, Never>?
    private var aiInsightsTask: Task<Void, Never>?
    private var lastInsightDate: Date?
    private var cancellables = Set<AnyCancellable>()

    public init(
        getScribblesForDateUseCase: GetScribblesForDateUseCase,
        addRawScribbleUseCase: AddRawScribbleUseCase,
        confirmScribbleUseCase: ConfirmScribbleUseCase,
        deleteScribbleUseCase: DeleteScribbleUseCase,
        parsePendingScribblesUseCase: ParsePendingScribblesUseCase,
        getAIOverviewUseCase: GetAIOverviewUseCase,
        configRepository: ConfigRepository
    ) {
        self.getScribblesForDateUseCase = getScribblesForDateUseCase
        self.addRawScribbleUseCase = addRawScribbleUseCase
        self.confirmScribbleUseCase = confirmScribbleUseCase
        self.deleteScribbleUseCase = deleteScribbleUseCase
        self.parsePendingScribblesUseCase = parsePendingScribblesUseCase
        self.getAIOverviewUseCase = getAIOverviewUseCase
        self.configRepository = configRepository
        
        setupConfigObservation()
        observeScribbles()
        refreshAIInsights(force: true)
    }

    public func onIntent(_ intent: CanvasIntent) {
        switch intent {
        case .updateScribbleText(let text):
            state.currentScribbleText = text
        case .addScribble(let text):
            addScribble(text)
        case .onPreviousDayClick:
            changeDate(by: -1)
        case .onNextDayClick:
            changeDate(by: 1)
        case .showDatePicker:
            state.isDatePickerVisible = true
        case .dismissDatePicker:
            state.isDatePickerVisible = false
        case .onDateSelected(let date):
            state.currentDate = date
            state.isDatePickerVisible = false
            observeScribbles()
            refreshAIInsights()
        case .toggleInputExpansion:
            state.isInputExpanded.toggle()
        case .clickOnScribble(let scribble):
            state.selectedScribble = scribble
        case .dismissScribbleDialog:
            state.selectedScribble = nil
        case .confirmScribble(let scribble):
            confirmScribble(scribble)
        case .deleteScribble(let id):
            deleteScribble(id)
        case .retryScribbleParsing(let scribble):
            retryParsing(scribble)
        case .navigateToSettings:
            state.isSettingsVisible = true
        case .updateExerciseName(let exerciseId, let newName):
            updateExerciseName(exerciseId: exerciseId, newName: newName)
        case .updateSetWeight(let exerciseId, let setId, let newWeight):
            updateSetWeight(exerciseId: exerciseId, setId: setId, newWeight: newWeight)
        case .updateSetReps(let exerciseId, let setId, let newReps):
            updateSetReps(exerciseId: exerciseId, setId: setId, newReps: newReps)
        default:
            break
        }
    }

    private func updateExerciseName(exerciseId: UUID, newName: String) {
        guard let selectedScribble = state.selectedScribble else { return }
        let updatedExercises = selectedScribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                return Exercise(
                    id: exercise.id,
                    canonicalName: newName,
                    muscleGroup: exercise.muscleGroup,
                    sets: exercise.sets,
                    isDraft: exercise.isDraft,
                    estimated1RM: exercise.estimated1RM,
                    intensity: exercise.intensity,
                    improvement: exercise.improvement
                )
            }
            return exercise
        }
        state.selectedScribble?.exercises = updatedExercises
    }

    private func updateSetWeight(exerciseId: UUID, setId: UUID, newWeight: String) {
        guard let selectedScribble = state.selectedScribble, let weight = Float(newWeight) else { return }
        let updatedExercises = selectedScribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                let updatedSets = exercise.sets.map { set in
                    if set.id == setId {
                        return ExerciseSet(
                            id: set.id,
                            setNumber: set.setNumber,
                            weight: weight,
                            reps: set.reps,
                            rpe: set.rpe,
                            notes: set.notes
                        )
                    }
                    return set
                }
                return Exercise(
                    id: exercise.id,
                    canonicalName: exercise.canonicalName,
                    muscleGroup: exercise.muscleGroup,
                    sets: updatedSets,
                    isDraft: exercise.isDraft,
                    estimated1RM: exercise.estimated1RM,
                    intensity: exercise.intensity,
                    improvement: exercise.improvement
                )
            }
            return exercise
        }
        state.selectedScribble?.exercises = updatedExercises
    }

    private func updateSetReps(exerciseId: UUID, setId: UUID, newReps: String) {
        guard let selectedScribble = state.selectedScribble, let reps = Int(newReps) else { return }
        let updatedExercises = selectedScribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                let updatedSets = exercise.sets.map { set in
                    if set.id == setId {
                        return ExerciseSet(
                            id: set.id,
                            setNumber: set.setNumber,
                            weight: set.weight,
                            reps: reps,
                            rpe: set.rpe,
                            notes: set.notes
                        )
                    }
                    return set
                }
                return Exercise(
                    id: exercise.id,
                    canonicalName: exercise.canonicalName,
                    muscleGroup: exercise.muscleGroup,
                    sets: updatedSets,
                    isDraft: exercise.isDraft,
                    estimated1RM: exercise.estimated1RM,
                    intensity: exercise.intensity,
                    improvement: exercise.improvement
                )
            }
            return exercise
        }
        state.selectedScribble?.exercises = updatedExercises
    }

    private func addScribble(_ text: String) {
        guard !text.isEmpty else { return }
        Task {
            do {
                try await addRawScribbleUseCase.execute(text: text, date: state.currentDate)
                state.currentScribbleText = ""
                // Auto-trigger parsing for the new scribble
                // We'll let the observer handle finding new pending scribbles in a real app,
                // but for now let's just trigger a parse check.
                triggerParsing()
            } catch {
                state.error = error.localizedDescription
            }
        }
    }

    private func confirmScribble(_ scribble: Scribble) {
        Task {
            do {
                try await confirmScribbleUseCase.execute(scribble: scribble)
                state.selectedScribble = nil
                // We don't refresh insights here anymore, only on date change
            } catch {
                state.error = error.localizedDescription
            }
        }
    }

    private func deleteScribble(_ id: UUID) {
        Task {
            do {
                try await deleteScribbleUseCase.execute(id: id)
                state.selectedScribble = nil
                // We don't refresh insights here anymore, only on date change
            } catch {
                state.error = error.localizedDescription
            }
        }
    }

    private func retryParsing(_ scribble: Scribble) {
        Task {
            do {
                try await parsePendingScribblesUseCase.parseSingleScribble(id: scribble.id)
            } catch {
                state.error = "Failed to retry parsing: \(error.localizedDescription)"
            }
        }
    }

    private func triggerParsing() {
        Task {
            // Find pending scribbles and parse them
            for scribble in state.scribbles where scribble.status == .pending {
                try? await parsePendingScribblesUseCase.parseSingleScribble(id: scribble.id)
            }
        }
    }

    private func refreshAIInsights(force: Bool = false) {
        // Token Optimization: Only fetch if date has changed or forced (initial load)
        let calendar = Calendar.current
        if !force, let lastDate = lastInsightDate, calendar.isDate(lastDate, inSameDayAs: state.currentDate) {
            return
        }
        
        lastInsightDate = state.currentDate
        aiInsightsTask?.cancel()
        state.isGeneratingInsights = true
        state.aiInsights = [] // Clear old insights while loading
        
        aiInsightsTask = Task {
            defer {
                if !Task.isCancelled {
                    state.isGeneratingInsights = false
                }
            }
            
            do {
                let insights = try await getAIOverviewUseCase.execute(date: state.currentDate)
                if !Task.isCancelled {
                    state.aiInsights = insights
                }
            } catch {
                // Silent failure for insights is usually better unless it's a critical error
                print("Failed to fetch AI insights: \(error)")
            }
        }
    }

    private func changeDate(by days: Int) {
        guard let newDate = Calendar.current.date(byAdding: .day, value: days, to: state.currentDate) else { return }
        
        let now = Date()
        if newDate > now { return }
        
        state.currentDate = newDate
        observeScribbles()
        refreshAIInsights()
    }

    private func observeScribbles() {
        observationTask?.cancel()
        observationTask = Task {
            let stream = getScribblesForDateUseCase.execute(date: state.currentDate)
            for await scribbles in stream {
                if Task.isCancelled { break }
                state.scribbles = scribbles
                
                // If we see pending scribbles, trigger parsing automatically
                if scribbles.contains(where: { $0.status == .pending }) {
                    triggerParsing()
                }
            }
        }
    }
    
    private func setupConfigObservation() {
        state.weightUnit = configRepository.getConfig().weightUnit
        
        configRepository.configPublisher
            .receive(on: RunLoop.main)
            .sink { [weak self] config in
                self?.state.weightUnit = config.weightUnit
            }
            .store(in: &cancellables)
    }
}
