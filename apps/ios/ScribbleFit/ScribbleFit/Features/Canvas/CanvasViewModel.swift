import Foundation
import SwiftUI
import Combine

public struct CanvasUiState: Sendable {
    public let greeting: String
    public let userName: String
    public let quickActions: [QuickActionType]
    public let homeSuggestion: AnalysisSuggestion?
    
    public init(
        greeting: String = "",
        userName: String = "George",
        quickActions: [QuickActionType] = [.repeatLast, .run5k, .restDay],
        homeSuggestion: AnalysisSuggestion? = nil
    ) {
        self.greeting = greeting
        self.userName = userName
        self.quickActions = quickActions
        self.homeSuggestion = homeSuggestion
    }
}

@MainActor
public final class CanvasViewModel: ObservableObject {
    private let canvasRepository: CanvasRepository
    private let analysisRepository: AnalysisRepository
    private let processScribbleUseCase: ProcessScribbleUseCase
    private let executeQuickActionUseCase: ExecuteQuickActionUseCase
    
    @Published public var scribbleText: String = ""
    @Published public var isSyncing: Bool = false
    @Published public var feedItems: [FeedItem] = []
    @Published public var uiState: CanvasUiState
    
    private var cancellables = Set<AnyCancellable>()
    
    public init(
        canvasRepository: CanvasRepository,
        analysisRepository: AnalysisRepository,
        processScribbleUseCase: ProcessScribbleUseCase,
        executeQuickActionUseCase: ExecuteQuickActionUseCase
    ) {
        self.canvasRepository = canvasRepository
        self.analysisRepository = analysisRepository
        self.processScribbleUseCase = processScribbleUseCase
        self.executeQuickActionUseCase = executeQuickActionUseCase
        
        self.uiState = CanvasUiState(greeting: Self.getGreeting())
        
        observeFeed()
        observeHomeSuggestion()
    }
    
    private func observeFeed() {
        // In a real app, the repository would provide a stream
        // For now, we refresh manually or via a timer
        refreshFeed()
    }
    
    private func observeHomeSuggestion() {
        Task {
            do {
                if let suggestion = try await analysisRepository.getHomeSuggestion() {
                    self.uiState = CanvasUiState(
                        greeting: self.uiState.greeting,
                        userName: self.uiState.userName,
                        quickActions: self.uiState.quickActions,
                        homeSuggestion: suggestion
                    )
                }
            } catch {
                print("Failed to fetch home suggestion: \(error)")
            }
        }
    }
    
    public func refreshFeed() {
        Task {
            do {
                self.feedItems = try await canvasRepository.getFeed()
            } catch {
                print("Failed to fetch feed: \(error)")
            }
        }
    }
    
    public func submitScribble() {
        let text = scribbleText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !text.isEmpty else { return }
        
        isSyncing = true
        Task {
            do {
                try await processScribbleUseCase.execute(rawText: text)
                scribbleText = ""
                self.feedItems = try await canvasRepository.getFeed()
            } catch {
                print("Failed to process scribble: \(error)")
            }
            isSyncing = false
        }
    }
    
    public func onQuickActionClick(_ actionType: QuickActionType) {
        Task {
            do {
                try await executeQuickActionUseCase.execute(actionType: actionType)
                self.feedItems = try await canvasRepository.getFeed()
            } catch {
                print("Failed to execute quick action: \(error)")
            }
        }
    }
    
    public func onRetryScribble(id: String) {
        Task {
            do {
                try await canvasRepository.retryScribble(id: id)
                self.feedItems = try await canvasRepository.getFeed()
            } catch {
                print("Failed to retry scribble: \(error)")
            }
        }
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
