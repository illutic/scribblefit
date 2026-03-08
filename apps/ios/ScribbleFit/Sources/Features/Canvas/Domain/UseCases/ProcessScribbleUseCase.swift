import Foundation

public final class ProcessScribbleUseCase: Sendable {
    private let canvasRepository: any CanvasRepository

    public init(canvasRepository: any CanvasRepository) {
        self.canvasRepository = canvasRepository
    }

    public func execute(rawText: String) async throws {
        try await canvasRepository.addScribble(rawText: rawText)
    }
}
