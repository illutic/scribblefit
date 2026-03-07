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
    
    public init(
        greeting: String = "",
        userName: String = "George",
        quickActions: [QuickActionType] = [.repeatLast, .run5k, .restDay],
        homeSuggestion: AnalysisSuggestion? = nil,
        scribbleText: String = "",
        feedItems: [FeedItem] = [],
        isSyncing: Bool = false
    ) {
        self.greeting = greeting
        self.userName = userName
        self.quickActions = quickActions
        self.homeSuggestion = homeSuggestion
        self.scribbleText = scribbleText
        self.feedItems = feedItems
        self.isSyncing = isSyncing
    }
}

@MainActor
public final class CanvasViewModel: ObservableObject {
    private let canvasRepository: CanvasRepository
    private let analysisRepository: AnalysisRepository
    private let processScribbleUseCase: ProcessScribbleUseCase
    private let executeQuickActionUseCase: ExecuteQuickActionUseCase
    
    @Published public var uiState: CanvasUiState
    
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
        
        refreshFeed()
        refreshSuggestion()
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
                await refreshFeed()
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
                await refreshFeed()
            } catch {
                print("Failed to execute quick action: \(error)")
            }
        }
    }
    
    public func onRetryScribble(id: String) {
        Task {
            do {
                try await canvasRepository.retryScribble(id: id)
                await refreshFeed()
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
