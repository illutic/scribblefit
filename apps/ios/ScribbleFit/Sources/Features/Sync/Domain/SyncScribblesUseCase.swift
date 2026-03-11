import Foundation

public final class SyncScribblesUseCase: Sendable {
    private let scribbleRepository: any ScribbleRepository
    private let engine: any LLMEngine

    public init(scribbleRepository: any ScribbleRepository, engine: any LLMEngine) {
        self.scribbleRepository = scribbleRepository
        self.engine = engine
    }

    public func execute() async {
        for await scribbles in scribbleRepository.getPendingScribbles().values {
            for scribble in scribbles {
                guard case .raw(let id, _, let rawText, _) = scribble else { continue }
                try? await scribbleRepository.updateSyncStatus(id: id, status: .processing)
                let result = await engine.parseWorkout(rawText: rawText)
                if result.status == .success {
                    try? await scribbleRepository.updateSyncStatus(id: id, status: .completed)
                } else {
                    try? await scribbleRepository.updateSyncStatus(id: id, status: .failed)
                }
            }
            break
        }
    }
}
