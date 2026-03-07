import Foundation

/**
 * Maps minimalist UI actions (e.g., "Repeat Last") to specific scribble injections.
 */
public final class ExecuteQuickActionUseCase {
    private let canvasRepository: CanvasRepository
    
    public init(canvasRepository: CanvasRepository) {
        self.canvasRepository = canvasRepository
    }
    
    public func execute(actionType: QuickActionType) async throws {
        let scribbleText = switch actionType {
        case .repeatLast: "Repeat last workout"
        case .restDay: "Today is a rest day"
        case .run5k: "Logged a 5k run"
        }
        try await canvasRepository.addScribble(rawText: scribbleText)
    }
}

public enum QuickActionType: String, Sendable {
    case repeatLast, restDay, run5k
}
