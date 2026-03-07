import Foundation
import SwiftUI
import Combine

@MainActor
public final class CanvasViewModel: ObservableObject {
    private let canvasRepository: CanvasRepository
    private let processScribbleUseCase: ProcessScribbleUseCase
    
    @Published public var scribbleText: String = ""
    @Published public var isSyncing: Bool = false
    @Published public var feedItems: [FeedItem] = []
    
    public init(
        canvasRepository: CanvasRepository,
        processScribbleUseCase: ProcessScribbleUseCase
    ) {
        self.canvasRepository = canvasRepository
        self.processScribbleUseCase = processScribbleUseCase
        
        refreshFeed()
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
                // Refresh feed immediately after adding
                self.feedItems = try await canvasRepository.getFeed()
            } catch {
                print("Failed to process scribble: \(error)")
            }
            isSyncing = false
        }
    }
}
