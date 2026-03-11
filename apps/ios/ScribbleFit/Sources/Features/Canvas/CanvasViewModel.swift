import Combine
import Foundation

public struct CanvasUiState: Sendable {
    public var greeting: String = ""
    public var userName: String = "George"
    public var scribbleText: String = ""
    public var scribbles: [Scribble] = []
    public var isSyncing: Bool = false
    public var isRecording: Bool = false
}

@MainActor
public final class CanvasViewModel: ObservableObject {
    @Published public var uiState = CanvasUiState()

    private let scribbleRepository: any ScribbleRepository
    private let confirmWorkoutUseCase: ConfirmWorkoutUseCase
    private var cancellables = Set<AnyCancellable>()

    public init(
        scribbleRepository: any ScribbleRepository,
        confirmWorkoutUseCase: ConfirmWorkoutUseCase
    ) {
        self.scribbleRepository = scribbleRepository
        self.confirmWorkoutUseCase = confirmWorkoutUseCase

        scribbleRepository.getAllScribbles()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] scribbles in
                self?.uiState.scribbles = scribbles
                self?.uiState.isSyncing = scribbles.contains {
                    if case .raw(_, _, _, let status) = $0 { return status == .pending }
                    return false
                }
            }
            .store(in: &cancellables)

        uiState.greeting = greeting()
    }

    public func onTextChange(_ newText: String) {
        uiState.scribbleText = newText
    }

    public func submitScribble() {
        let text = uiState.scribbleText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !text.isEmpty else { return }
        Task {
            await scribbleRepository.enqueueScribble(rawText: text)
            uiState.scribbleText = ""
        }
    }

    public func onRetryScribble(id: String) {
        Task { try? await scribbleRepository.updateSyncStatus(id: id, status: .pending) }
    }

    public func onConfirmClick(parsedWorkout: ParsedWorkout, scribbleId: String) {
        Task { try? await confirmWorkoutUseCase.execute(parsedWorkout: parsedWorkout, scribbleId: scribbleId) }
    }

    public func onMicClick() {
        uiState.isRecording.toggle()
    }

    private func greeting() -> String {
        let hour = Calendar.current.component(.hour, from: Date())
        switch hour {
        case 0..<12: return "Good morning"
        case 12..<17: return "Good afternoon"
        default: return "Good evening"
        }
    }
}
