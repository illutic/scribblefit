import Combine
import Foundation

public struct CanvasUiState: Sendable {
    public var greeting: String = ""
    public var userName: String = "George"
    public var quickActions: [QuickActionType] = QuickActionType.allCases
    public var homeSuggestion: AnalysisSuggestion? = nil
    public var scribbleText: String = ""
    public var feedItems: [FeedItem] = []
    public var isSyncing: Bool = false
    public var isRecording: Bool = false
}

@MainActor
public final class CanvasViewModel: ObservableObject {
    @Published public var uiState = CanvasUiState()

    private let canvasRepository: any CanvasRepository
    private let processScribbleUseCase: ProcessScribbleUseCase
    private let confirmWorkoutUseCase: ConfirmWorkoutUseCase
    private let executeQuickActionUseCase: ExecuteQuickActionUseCase
    private var cancellables = Set<AnyCancellable>()

    public init(
        canvasRepository: any CanvasRepository,
        processScribbleUseCase: ProcessScribbleUseCase,
        confirmWorkoutUseCase: ConfirmWorkoutUseCase,
        executeQuickActionUseCase: ExecuteQuickActionUseCase
    ) {
        self.canvasRepository = canvasRepository
        self.processScribbleUseCase = processScribbleUseCase
        self.confirmWorkoutUseCase = confirmWorkoutUseCase
        self.executeQuickActionUseCase = executeQuickActionUseCase

        canvasRepository.getFeed()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] items in self?.uiState.feedItems = items }
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
            try? await processScribbleUseCase.execute(rawText: text)
            uiState.scribbleText = ""
        }
    }

    public func onQuickActionClick(_ type: QuickActionType) {
        Task { try? await executeQuickActionUseCase.execute(type: type) }
    }

    public func onRetryScribble(id: String) {
        Task { try? await canvasRepository.retryScribble(id: id) }
    }

    public func onConfirmClick(confirmation: ConfirmationItem) {
        Task { try? await confirmWorkoutUseCase.execute(confirmation: confirmation) }
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
