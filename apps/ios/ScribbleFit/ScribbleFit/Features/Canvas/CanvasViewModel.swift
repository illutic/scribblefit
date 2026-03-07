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
    private let analysisRepository: AnalysisRepository
    private let processScribbleUseCase: ProcessScribbleUseCase
    private let executeQuickActionUseCase: ExecuteQuickActionUseCase
    private let confirmWorkoutUseCase: ConfirmWorkoutUseCase
    
    @Published public var uiState: CanvasUiState
    
    public init(
        canvasRepository: CanvasRepository,
        analysisRepository: AnalysisRepository,
        processScribbleUseCase: ProcessScribbleUseCase,
        executeQuickActionUseCase: ExecuteQuickActionUseCase,
        confirmWorkoutUseCase: ConfirmWorkoutUseCase
    ) {
        self.canvasRepository = canvasRepository
        self.analysisRepository = analysisRepository
        self.processScribbleUseCase = processScribbleUseCase
        self.executeQuickActionUseCase = executeQuickActionUseCase
        self.confirmWorkoutUseCase = confirmWorkoutUseCase
        
        self.uiState = CanvasUiState(greeting: Self.getGreeting())
        
        refreshFeed()
        refreshSuggestion()
        
        Task {
            if try await canvasRepository.getFeed().isEmpty {
                await seedTestData()
            }
        }
    }
    
    private func seedTestData() async {
        let now = Date()
        
        try? await canvasRepository.addScribble(rawText: "Ready for a Push day? 💪")
        
        let scribbleId = UUID().uuidString
        try? await canvasRepository.addScribble(rawText: "Bench 135x5, 135x5")
        
        try? await canvasRepository.addConfirmation(item: ConfirmationItem(
            id: UUID().uuidString,
            timestamp: now.addingTimeInterval(1),
            workout: ParsedWorkout(
                date: "2024-05-20",
                location: "Home Gym",
                exercises: [
                    ParsedExercise(
                        canonicalName: "Bench Press",
                        sets: [
                            ParsedSet(weight: 135.0, reps: 5),
                            ParsedSet(weight: 135.0, reps: 5)
                        ]
                    )
                ]
            ),
            scribbleId: scribbleId
        ))
        
        try? await canvasRepository.addInsight(item: InsightItem(
            id: UUID().uuidString,
            timestamp: now.addingTimeInterval(2),
            text: "New Volume PR on Bench! 🔥",
            emoji: "🏆"
        ))
        
        refreshFeed()
    }
    
    public func refreshFeed() {
        Task {
            do {
                let items = try await canvasRepository.getFeed()
                self.uiState.feedItems = items
            } catch {
                print("Failed to fetch feed: \(error)")
            }
        }
    }
    
    private func refreshSuggestion() {
        Task {
            do {
                if let suggestion = try await analysisRepository.getHomeSuggestion() {
                    self.uiState.homeSuggestion = suggestion
                }
            } catch {
                print("Failed to fetch suggestion: \(error)")
            }
        }
    }
    
    public func onTextChange(_ newText: String) {
        uiState.scribbleText = newText
    }
    
    public func submitScribble() {
        let text = uiState.scribbleText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !text.isEmpty else { return }
        
        uiState.isSyncing = true
        Task {
            do {
                try await processScribbleUseCase.execute(rawText: text)
                uiState.scribbleText = ""
                refreshFeed()
            } catch {
                print("Failed to process scribble: \(error)")
            }
            uiState.isSyncing = false
        }
    }
    
    public func onQuickActionClick(_ actionType: QuickActionType) {
        Task {
            do {
                try await executeQuickActionUseCase.execute(actionType: actionType)
                refreshFeed()
            } catch {
                print("Failed to execute quick action: \(error)")
            }
        }
    }
    
    public func onRetryScribble(id: String) {
        Task {
            do {
                try await canvasRepository.retryScribble(id: id)
                refreshFeed()
            } catch {
                print("Failed to retry scribble: \(error)")
            }
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
                refreshFeed()
            } catch {
                print("Failed to confirm workout: \(error)")
            }
        }
    }

    public func onMicClick() {
        if uiState.isRecording {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private func startRecording() {
        uiState.isRecording = true
    }

    private func stopRecording() {
        uiState.isRecording = false
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
