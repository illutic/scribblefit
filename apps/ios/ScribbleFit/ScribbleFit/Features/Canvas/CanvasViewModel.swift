import Foundation
import SwiftUI
import Combine

public struct CanvasUiState: Sendable {
    public var greeting: String
    public var userName: String
    public var quickActions: [QuickActionType]
    public var homeSuggestion: AnalysisSuggestion?
    public var scribbleText: String
    public var feedItems: [FeedItem]
    public var isSyncing: Bool
    public var isRecording: Bool

    public init(
        greeting: String = "",
        userName: String = "George",
        quickActions: [QuickActionType] = [.repeatLast, .run5k, .restDay],
        homeSuggestion: AnalysisSuggestion? = nil,
        scribbleText: String = "",
        feedItems: [FeedItem] = [],
        isSyncing: Bool = false,
        isRecording: Bool = false
    ) {
        self.greeting = greeting
        self.userName = userName
        self.quickActions = quickActions
        self.homeSuggestion = homeSuggestion
        self.scribbleText = scribbleText
        self.feedItems = feedItems
        self.isSyncing = isSyncing
        self.isRecording = isRecording
    }
}

@MainActor
public final class CanvasViewModel: ObservableObject {
    private let canvasRepository: CanvasRepository
    private let processScribbleUseCase: ProcessScribbleUseCase
    private let executeQuickActionUseCase: ExecuteQuickActionUseCase
    private let confirmWorkoutUseCase: ConfirmWorkoutUseCase
    private let listenForSyncItemsUseCase: ListenForSyncItemsUseCase

    private let _internalState: CurrentValueSubject<CanvasUiState, Never>
    @Published public private(set) var uiState: CanvasUiState

    public init(
        canvasRepository: CanvasRepository,
        analysisRepository: AnalysisRepository,
        processScribbleUseCase: ProcessScribbleUseCase,
        executeQuickActionUseCase: ExecuteQuickActionUseCase,
        confirmWorkoutUseCase: ConfirmWorkoutUseCase,
        listenForSyncItemsUseCase: ListenForSyncItemsUseCase
    ) {
        self.canvasRepository = canvasRepository
        self.processScribbleUseCase = processScribbleUseCase
        self.executeQuickActionUseCase = executeQuickActionUseCase
        self.confirmWorkoutUseCase = confirmWorkoutUseCase
        self.listenForSyncItemsUseCase = listenForSyncItemsUseCase

        let initial = CanvasUiState(greeting: Self.getGreeting())
        self._internalState = CurrentValueSubject(initial)
        self.uiState = initial

        Publishers.CombineLatest3(
            _internalState,
            canvasRepository.getFeed(),
            analysisRepository.getHomeSuggestion()
        )
        .map { state, feed, suggestion in
            var updated = state
            updated.feedItems = feed
            updated.homeSuggestion = suggestion
            return updated
        }
        .receive(on: RunLoop.main)
        .assign(to: &$uiState)

        Task {
            await listenForSyncItemsUseCase.execute()
        }
    }

    public func onTextChange(_ newText: String) {
        var state = _internalState.value
        state.scribbleText = newText
        _internalState.send(state)
    }

    public func submitScribble() {
        let text = _internalState.value.scribbleText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !text.isEmpty else { return }

        Task {
            var state = _internalState.value
            state.isSyncing = true
            _internalState.send(state)

            try? await processScribbleUseCase.execute(rawText: text)

            state = _internalState.value
            state.isSyncing = false
            state.scribbleText = ""
            _internalState.send(state)
        }
    }

    public func onQuickActionClick(_ actionType: QuickActionType) {
        Task {
            try? await executeQuickActionUseCase.execute(actionType: actionType)
        }
    }

    public func onRetryScribble(id: String) {
        Task {
            try? await canvasRepository.retryScribble(id: id)
        }
    }

    public func onConfirmClick(confirmation: ConfirmationItem) {
        Task {
            do {
                try await confirmWorkoutUseCase.execute(workout: confirmation.workout)
                try await canvasRepository.addInsight(item: InsightItem(
                    id: UUID().uuidString,
                    timestamp: Date(),
                    text: "Workout saved to ledger!",
                    emoji: "✅"
                ))
                try await canvasRepository.removeFeedItem(id: confirmation.id)
            } catch {
                print("Failed to confirm workout: \(error)")
            }
        }
    }

    public func onMicClick() {
        if _internalState.value.isRecording {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private func startRecording() {
        var state = _internalState.value
        state.isRecording = true
        _internalState.send(state)
    }

    private func stopRecording() {
        var state = _internalState.value
        state.isRecording = false
        _internalState.send(state)
        onTextChange("Bench 135x5, 135x5")
    }

    private static func getGreeting() -> String {
        let hour = Calendar.current.component(.hour, from: Date())
        switch hour {
        case 0...11: return "MORNING"
        case 12...16: return "AFTERNOON"
        case 17...20: return "EVENING"
        default: return "NIGHT"
        }
    }
}
