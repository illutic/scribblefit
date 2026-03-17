import Foundation
import Observation

@Observable
@MainActor
public final class CanvasStore {
    public private(set) var state = CanvasState()

    private let getScribblesByDateUseCase: GetScribblesByDateUseCase
    private let addRawScribbleUseCase: AddRawScribbleUseCase
    // private let editScribbleUseCase: EditScribbleUseCase
    // private let removeScribbleUseCase: RemoveScribbleUseCase
    // private let updateScribbleAsCompleteUseCase: UpdateScribbleAsCompleteUseCase
    
    private var scribbleTask: Task<Void, Never>?

    public init(
        getScribblesByDateUseCase: GetScribblesByDateUseCase,
        addRawScribbleUseCase: AddRawScribbleUseCase
    ) {
        self.getScribblesByDateUseCase = getScribblesByDateUseCase
        self.addRawScribbleUseCase = addRawScribbleUseCase
        
        observeScribbles()
    }

    public func onIntent(_ intent: CanvasIntent) {
        switch intent {
        case .updateScribbleText(let text):
            state.currentScribbleText = text

        case .addScribble(let text):
            addScribble(text)

        case .clickOnScribble(let scribble):
            scribbleClicked(scribble)

        case .onPreviousDayClick:
            state.currentDate = Calendar.current.date(byAdding: .day, value: -1, to: state.currentDate) ?? state.currentDate
            observeScribbles()

        case .onNextDayClick:
            state.currentDate = Calendar.current.date(byAdding: .day, value: 1, to: state.currentDate) ?? state.currentDate
            observeScribbles()

        case .confirmScribble(_):
            // completeScribble(scribble)
            dismissScribbleDialog()

        case .deleteScribble(_):
            // deleteScribble(scribble)
            dismissScribbleDialog()

        case .dismissScribbleDialog:
            dismissScribbleDialog()

        case .updateScribble(let scribble):
            editScribble(scribble)

        case .navigateBack:
            // navigator.goBack()
            break
            
        case .navigateToProfile:
            // navigator.navigateTo(.profile)
            break
        }
    }

    private func observeScribbles() {
        scribbleTask?.cancel()
        scribbleTask = Task {
            for await scribbles in getScribblesByDateUseCase.execute(date: state.currentDate) {
                state.scribbles = scribbles
            }
        }
    }

    private func addScribble(_ text: String) {
        guard !text.isEmpty else { return }

        Task {
            let editingId = state.editingScribbleId
            state.currentScribbleText = ""
            state.editingScribbleId = nil

            do {
                if let _ = editingId {
                    // try await editScribbleUseCase.execute(id: editingId, newText: text)
                } else {
                    try await addRawScribbleUseCase.execute(text: text, date: state.currentDate)
                }
            } catch {
                state.error = error
            }
        }
    }

    private func scribbleClicked(_ scribble: Scribble) {
        switch scribble.status {
        case .failed:
            // retry parsing
            break
        case .parsed:
            state.selectedScribble = scribble
        default:
            break
        }
    }

    private func dismissScribbleDialog() {
        state.selectedScribble = nil
    }

    private func editScribble(_ scribble: Scribble) {
        state.currentScribbleText = scribble.rawText
        state.editingScribbleId = scribble.id
        state.selectedScribble = nil
    }
}
