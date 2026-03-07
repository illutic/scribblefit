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
    
    private var cancellables = Set<AnyCancellable>()
    
    public init(
        canvasRepository: CanvasRepository,
        processScribbleUseCase: ProcessScribbleUseCase
    ) {
        self.canvasRepository = canvasRepository
        self.processScribbleUseCase = processScribbleUseCase
        
        observeFeed()
    }
    
    private func observeFeed() {
        Task {
            // Using Task to poll or observe the repository
            // In a real SwiftData app, we'd use @Query or a PassthroughSubject
            // For now, let's just do an initial fetch
            do {
                self.feedItems = try await canvasRepository.getFeed()
            } catch {
                print("Failed to fetch feed: \(error)")
            }
        }
    }
    
    public func submitScribble() {
        let text = scribbleText
        guard !text.isEmpty else { return }
        
        isSyncing = true
        Task {
            do {
                try await processScribbleUseCase.execute(rawText: text)
                scribbleText = ""
                // Refresh feed after adding
                self.feedItems = try await canvasRepository.getFeed()
            } catch {
                print("Failed to process scribble: \(error)")
            }
            isSyncing = false
        }
    }
}
