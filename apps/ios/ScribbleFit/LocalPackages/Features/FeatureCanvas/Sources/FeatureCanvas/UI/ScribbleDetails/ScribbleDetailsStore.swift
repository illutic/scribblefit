import Foundation
import Observation
import CoreModel
import FeatureScribble

@Observable
@MainActor
public final class ScribbleDetailsStore {
    public var state: ScribbleDetailsState
    
    private let scribbleRepository: ScribbleRepository
    private let configRepository: ConfigRepository
    private let confirmScribbleUseCase: ConfirmScribbleUseCase
    
    private let scribbleId: UUID?

    public init(
        scribble: Scribble? = nil,
        scribbleId: UUID? = nil,
        scribbleRepository: ScribbleRepository,
        configRepository: ConfigRepository,
        confirmScribbleUseCase: ConfirmScribbleUseCase
    ) {
        self.state = ScribbleDetailsState(
            scribble: scribble,
            weightUnit: configRepository.getConfig().weightUnit
        )
        self.scribbleId = scribbleId
        self.scribbleRepository = scribbleRepository
        self.configRepository = configRepository
        self.confirmScribbleUseCase = confirmScribbleUseCase
        
        if let id = scribbleId {
            Task {
                await loadScribble(id)
            }
        }
    }

    public func onIntent(_ intent: ScribbleDetailsIntent) {
        switch intent {
        case .loadScribble(let id):
            Task {
                await loadScribble(id)
            }
        case .logScribble:
            Task {
                await logScribble()
            }
        case .dismiss:
            // Handled by view presentation logic
            break
        case .exerciseTapped(let name):
            state = state.copy(selectedExerciseName: name)
        case .dismissExerciseDetails:
            state = state.copy(selectedExerciseName: .some(nil))
        }
    }

    private func loadScribble(_ id: UUID) async {
        state = state.copy(isLoading: true)
        do {
            if let scribble = try await scribbleRepository.getScribble(id: id) {
                state = state.copy(scribble: scribble, isLoading: false)
            } else {
                state = state.copy(isLoading: false, error: "Scribble not found")
            }
        } catch {
            state = state.copy(isLoading: false, error: error.localizedDescription)
        }
    }

    private func logScribble() async {
        guard let scribble = state.scribble else { return }
        state = state.copy(isLoading: true)
        do {
            try await confirmScribbleUseCase.execute(scribble: scribble)
            // Reload to get updated status
            await loadScribble(scribble.id)
        } catch {
            state = state.copy(isLoading: false, error: error.localizedDescription)
        }
    }
}
