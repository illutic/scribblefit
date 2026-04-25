import Foundation
import CoreModel
import CoreCommon

@MainActor
public final class ConfirmScribbleUseCase {
    private let scribbleRepository: ScribbleRepository

    public init(scribbleRepository: ScribbleRepository) {
        self.scribbleRepository = scribbleRepository
    }

    public func execute(scribble: Scribble) async throws {
        if scribble.status != .success {
            throw ScribbleError.invalidStatus(scribble.status)
        }

        var completedScribble = scribble
        completedScribble.status = .completed

        // In the new architecture, confirming a scribble just means updating its status to `.completed`.
        // The repository's `confirmScribble` method handles syncing exercises and saving.
        try await scribbleRepository.confirmScribble(completedScribble)
    }
}
