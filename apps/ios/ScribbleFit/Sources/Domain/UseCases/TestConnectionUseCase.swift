import Foundation

@MainActor
public final class TestConnectionUseCase {
    private let llmService: LLMService

    public init(llmService: LLMService) {
        self.llmService = llmService
    }

    public func execute(apiKey: String) async throws {
        try await llmService.validateApiKey(apiKey)
    }
}
