import Foundation

public enum QuickActionType: String, CaseIterable, Sendable {
    case repeatLast = "Repeat last"
    case restDay = "Rest day"
    case run5k = "Run 5k"
}

public final class ExecuteQuickActionUseCase: Sendable {
    private let canvasRepository: any CanvasRepository

    public init(canvasRepository: any CanvasRepository) {
        self.canvasRepository = canvasRepository
    }

    public func execute(type: QuickActionType) async throws {
        try await canvasRepository.addScribble(rawText: type.rawValue)
    }
}
