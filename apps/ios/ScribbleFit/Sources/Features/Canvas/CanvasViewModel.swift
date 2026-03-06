import Foundation
import SwiftUI

@MainActor
public final class CanvasViewModel: ObservableObject {
    private let syncRepository: SyncRepository
    
    @Published public var scribbleText: String = ""
    @Published public var isSyncing: Bool = false
    
    public init(syncRepository: SyncRepository) {
        self.syncRepository = syncRepository
    }
    
    public func submitScribble() {
        let text = scribbleText
        guard !text.isEmpty else { return }
        
        isSyncing = true
        Task {
            do {
                try await syncRepository.enqueueScribble(rawText: text)
                scribbleText = ""
            } catch {
                print("Failed to enqueue scribble: \(error)")
            }
            isSyncing = false
        }
    }
}
