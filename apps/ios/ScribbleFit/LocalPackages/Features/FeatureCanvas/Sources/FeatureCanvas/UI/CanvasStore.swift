import Foundation
import Observation
import Combine
import CoreModel
import FeatureScribble
import FeatureInsights
import FeatureSets

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
    private let updateScribbleWithWorkoutUseCase: UpdateScribbleWithWorkoutUseCase
    private let manualEditScribbleUseCase: ManualEditScribbleUseCase
    private let reorderSetsUseCase: ReorderSetsUseCase
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
        updateScribbleWithWorkoutUseCase: UpdateScribbleWithWorkoutUseCase,
        manualEditScribbleUseCase: ManualEditScribbleUseCase,
        reorderSetsUseCase: ReorderSetsUseCase,
        configRepository: ConfigRepository
    ) {
        self.getScribblesForDateUseCase = getScribblesForDateUseCase
        self.addRawScribbleUseCase = addRawScribbleUseCase
        self.confirmScribbleUseCase = confirmScribbleUseCase
        self.deleteScribbleUseCase = deleteScribbleUseCase
        self.parsePendingScribblesUseCase = parsePendingScribblesUseCase
        self.getAIOverviewUseCase = getAIOverviewUseCase
        self.updateScribbleWithWorkoutUseCase = updateScribbleWithWorkoutUseCase
        self.manualEditScribbleUseCase = manualEditScribbleUseCase
        self.reorderSetsUseCase = reorderSetsUseCase
        self.configRepository = configRepository
        
        setupConfigObservation()
        observeScribbles()
        refreshAIInsights(force: true)
    }

    public func onIntent(_ intent: CanvasIntent) {
        switch intent {
        case .updateScribbleText, .addScribble, .clickOnScribble, .dismissScribbleDialog, 
             .confirmScribble, .deleteScribble, .retryScribbleParsing:
            handleScribbleIntent(intent)
            
        case .onPreviousDayClick, .onNextDayClick, .showDatePicker, .dismissDatePicker, .onDateSelected:
            handleDateIntent(intent)
            
        case .updateExerciseName, .updateSetWeight, .updateSetReps, .deleteSet:
            handleEditIntent(intent)
            
        case .navigateToSettings, .dismissSettings, .toggleInputExpansion:
            handleUIIntent(intent)
            
        default:
            break
        }
    }

    private func handleScribbleIntent(_ intent: CanvasIntent) {
        switch intent {
        case .updateScribbleText(let text):
            state = state.copy(currentScribbleText: text)
        case .addScribble(let text):
            addScribble(text)
        case .clickOnScribble(let scribble):
            if scribble.status == .success {
                state = state.copy(selectedScribble: scribble)
            } else if scribble.status == .completed, let workoutId = scribble.workoutId {
                state = state.copy(navigationState: .workoutExercises(workoutId))
            }
        case .dismissScribbleDialog:
            state = state.copy(selectedScribble: .some(nil))
        case .confirmScribble(let scribble):
            confirmScribble(scribble)
        case .deleteScribble(let id):
            deleteScribble(id)
        case .retryScribbleParsing(let scribble):
            retryParsing(scribble)
        default:
            break
        }
    }

    private func handleDateIntent(_ intent: CanvasIntent) {
        switch intent {
        case .onPreviousDayClick:
            changeDate(by: -1)
        case .onNextDayClick:
            changeDate(by: 1)
        case .showDatePicker:
            state = state.copy(isDatePickerVisible: true)
        case .dismissDatePicker:
            state = state.copy(isDatePickerVisible: false)
        case .onDateSelected(let date):
            let startOfToday = Calendar.current.startOfDay(for: Date())
            let startOfSelected = Calendar.current.startOfDay(for: date)
            
            if startOfSelected <= startOfToday {
                state = state.copy(currentDate: date, isDatePickerVisible: false)
                observeScribbles()
                refreshAIInsights()
            } else {
                state = state.copy(isDatePickerVisible: false)
            }
        default:
            break
        }
    }

    private func handleEditIntent(_ intent: CanvasIntent) {
        switch intent {
        case .updateExerciseName(let exerciseId, let newName):
            updateExerciseName(exerciseId: exerciseId, newName: newName)
        case .updateSetWeight(let exerciseId, let setId, let newWeight):
            updateSetWeight(exerciseId: exerciseId, setId: setId, newWeight: newWeight)
        case .updateSetReps(let exerciseId, let setId, let newReps):
            updateSetReps(exerciseId: exerciseId, setId: setId, newReps: newReps)
        case .deleteSet(let exerciseId, let setId):
            deleteSet(exerciseId: exerciseId, setId: setId)
        default:
            break
        }
    }

    private func handleUIIntent(_ intent: CanvasIntent) {
        switch intent {
        case .navigateToSettings:
            state = state.copy(isSettingsVisible: true)
        case .dismissSettings:
            state = state.copy(isSettingsVisible: false)
        case .toggleInputExpansion:
            state = state.copy(isInputExpanded: !state.isInputExpanded)
        case .navigateToExerciseDetails(let name):
            state = state.copy(navigationState: .exerciseDetails(name))
        case .navigateToWorkoutExercises(let id):
            state = state.copy(navigationState: .workoutExercises(id))
        case .dismissDetails:
            state = state.copy(navigationState: .some(nil))
        default:
            break
        }
    }

    private func updateExerciseName(exerciseId: UUID, newName: String) {
        guard let selectedScribble = state.selectedScribble else { return }
        Task {
            try? await manualEditScribbleUseCase.updateExerciseName(
                scribbleId: selectedScribble.id,
                exerciseId: exerciseId,
                newName: newName
            )
            // Local update for immediate UI feedback
            if let selected = state.selectedScribble {
                let updated = selected.copy(exercises: selected.exercises.map { ex in
                    ex.id == exerciseId ? ex.copy(canonicalName: newName) : ex
                })
                state = state.copy(selectedScribble: updated)
            }
        }
    }

    private func updateSetWeight(exerciseId: UUID, setId: UUID, newWeight: String) {
        guard let selectedScribble = state.selectedScribble, let weight = Float(newWeight) else { return }
        Task {
            try? await manualEditScribbleUseCase.updateSetWeight(
                scribbleId: selectedScribble.id,
                exerciseId: exerciseId,
                setId: setId,
                newWeight: weight
            )
            // Local update
            if let selected = state.selectedScribble {
                let updated = selected.copy(exercises: selected.exercises.map { ex in
                    if ex.id == exerciseId {
                        return ex.copy(sets: ex.sets.map { s in
                            s.id == setId ? s.copy(weight: weight) : s
                        })
                    }
                    return ex
                })
                state = state.copy(selectedScribble: updated)
            }
        }
    }

    private func updateSetReps(exerciseId: UUID, setId: UUID, newReps: String) {
        guard let selectedScribble = state.selectedScribble, let reps = Int(newReps) else { return }
        Task {
            try? await manualEditScribbleUseCase.updateSetReps(
                scribbleId: selectedScribble.id,
                exerciseId: exerciseId,
                setId: setId,
                newReps: reps
            )
            // Local update
            if let selected = state.selectedScribble {
                let updated = selected.copy(exercises: selected.exercises.map { ex in
                    if ex.id == exerciseId {
                        return ex.copy(sets: ex.sets.map { s in
                            s.id == setId ? s.copy(reps: reps) : s
                        })
                    }
                    return ex
                })
                state = state.copy(selectedScribble: updated)
            }
        }
    }

    private func deleteSet(exerciseId: UUID, setId: UUID) {
        guard let selectedScribble = state.selectedScribble else { return }
        Task {
            try? await manualEditScribbleUseCase.deleteSet(
                scribbleId: selectedScribble.id,
                exerciseId: exerciseId,
                setId: setId
            )
            // Local update handled via re-observation usually, but for immediate UI:
            if let selected = state.selectedScribble {
                let updated = selected.copy(exercises: selected.exercises.map { ex in
                    if ex.id == exerciseId {
                        return ex.copy(sets: ex.sets.filter { $0.id != setId })
                    }
                    return ex
                })
                state = state.copy(selectedScribble: updated)
            }
        }
    }

    private func addScribble(_ text: String) {
        guard !text.isEmpty else { return }
        Task {
            do {
                try await addRawScribbleUseCase.execute(text: text, date: state.currentDate)
                state = state.copy(currentScribbleText: "")
                triggerParsing()
            } catch {
                state = state.copy(error: error.localizedDescription)
            }
        }
    }

    private func confirmScribble(_ scribble: Scribble) {
        Task {
            do {
                try await confirmScribbleUseCase.execute(scribble: scribble)
                state = state.copy(selectedScribble: .some(nil))
            } catch {
                state = state.copy(error: error.localizedDescription)
            }
        }
    }

    private func deleteScribble(_ id: UUID) {
        Task {
            do {
                try await deleteScribbleUseCase.execute(id: id)
                state = state.copy(selectedScribble: .some(nil))
            } catch {
                state = state.copy(error: error.localizedDescription)
            }
        }
    }

    private func retryParsing(_ scribble: Scribble) {
        Task {
            do {
                try await parsePendingScribblesUseCase.parseSingleScribble(id: scribble.id)
            } catch {
                state = state.copy(error: "Failed to retry parsing: \(error.localizedDescription)")
            }
        }
    }

    private func triggerParsing() {
        Task {
            for scribble in state.scribbles where scribble.status == .pending {
                try? await parsePendingScribblesUseCase.parseSingleScribble(id: scribble.id)
            }
        }
    }

    private func refreshAIInsights(force: Bool = false) {
        let calendar = Calendar.current
        if !force, let lastDate = lastInsightDate, calendar.isDate(lastDate, inSameDayAs: state.currentDate) {
            return
        }
        
        lastInsightDate = state.currentDate
        aiInsightsTask?.cancel()
        state = state.copy(aiInsights: [], isGeneratingInsights: true)
        
        aiInsightsTask = Task {
            defer {
                if !Task.isCancelled {
                    state = state.copy(isGeneratingInsights: false)
                }
            }
            
            do {
                let insights = try await getAIOverviewUseCase.execute(date: state.currentDate)
                if !Task.isCancelled {
                    state = state.copy(aiInsights: insights)
                }
            } catch {
                print("Failed to fetch AI insights: \(error)")
            }
        }
    }

    private func changeDate(by days: Int) {
        guard let newDate = Calendar.current.date(byAdding: .day, value: days, to: state.currentDate) else { return }
        
        let startOfToday = Calendar.current.startOfDay(for: Date())
        let startOfNewDate = Calendar.current.startOfDay(for: newDate)
        
        if startOfNewDate > startOfToday { return }
        
        state = state.copy(currentDate: newDate)
        observeScribbles()
        refreshAIInsights()
    }

    private func observeScribbles() {
        observationTask?.cancel()
        observationTask = Task {
            let stream = getScribblesForDateUseCase.execute(date: state.currentDate)
            for await scribbles in stream {
                if Task.isCancelled { break }
                state = state.copy(scribbles: scribbles)
                
                if scribbles.contains(where: { $0.status == .pending }) {
                    triggerParsing()
                }
                
                // Keep selectedScribble in sync if it's being edited
                if let selected = state.selectedScribble,
                   let updated = scribbles.first(where: { $0.id == selected.id }) {
                    state = state.copy(selectedScribble: updated)
                }
            }
        }
    }
    
    private func setupConfigObservation() {
        state = state.copy(weightUnit: configRepository.getConfig().weightUnit)
        
        configRepository.configPublisher
            .receive(on: RunLoop.main)
            .sink { [weak self] config in
                self?.state = self?.state.copy(weightUnit: config.weightUnit) ?? .init()
            }
            .store(in: &cancellables)
    }
}
