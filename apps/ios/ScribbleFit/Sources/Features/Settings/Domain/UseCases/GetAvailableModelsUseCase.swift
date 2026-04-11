import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

@MainActor
public final class GetAvailableModelsUseCase {
    private let llmService: LLMService

    public init(llmService: LLMService) {
        self.llmService = llmService
    }

    public func execute(apiKey: String) async throws -> [String] {
        return try await llmService.getAvailableModels(apiKey: apiKey)
    }
}
