import Foundation
import CoreModel
import FeatureScribble

@MainActor
public final class DeleteScribbleUseCase {
    private let removeScribbleUseCase: RemoveScribbleUseCase

    public init(removeScribbleUseCase: RemoveScribbleUseCase) {
        self.removeScribbleUseCase = removeScribbleUseCase
    }

    public func execute(id: UUID) async throws {
        try await removeScribbleUseCase.execute(id: id)
    }
}
