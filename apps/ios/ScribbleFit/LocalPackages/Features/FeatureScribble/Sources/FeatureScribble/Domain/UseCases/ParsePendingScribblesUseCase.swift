import Foundation
import CoreModel

@MainActor
public final class ParsePendingScribblesUseCase {
    private let scribbleRepository: ScribbleRepository
    private let llmProvider: LLMService

    public init(scribbleRepository: ScribbleRepository, llmProvider: LLMService) {
        self.scribbleRepository = scribbleRepository
        self.llmProvider = llmProvider
    }

    public func execute(date: Date) async {
        // We get the stream and take the first snapshot of pending ones
        // In a real app, we'd probably want to subscribe to the stream or handle this more reactively
        // But for now, let's just parse what's currently pending.
        
        // This is a bit tricky with AsyncStream since it doesn't always finish immediately
        // For now, let's assume we can parse them one by one if we have their IDs.
    }
    
    public func parseSingleScribble(id: UUID) async throws {
        guard var scribble = try await scribbleRepository.getScribble(id: id) else { return }
        
        scribble.status = .parsing
        try await scribbleRepository.updateScribble(scribble)
        
        do {
            let result = try await llmProvider.parseWorkout(rawText: scribble.rawText)
            
            scribble.status = .success
            scribble.exercises = result.exercises
            // We might want to store the parsed JSON too if needed
            
            try await scribbleRepository.updateScribble(scribble)
        } catch {
            scribble.status = .failed
            try await scribbleRepository.updateScribble(scribble)
            throw error
        }
    }
}
