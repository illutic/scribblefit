import Foundation

/**
 * Orchestrates adding a raw user entry to the feed and triggering background parsing.
 */
public final class ProcessScribbleUseCase {
    private let canvasRepository: CanvasRepository
    
    public init(canvasRepository: CanvasRepository) {
        self.canvasRepository = canvasRepository
    }
    
    public func execute(rawText: String) async throws {
        try await canvasRepository.addScribble(rawText: rawText)
    }
}
