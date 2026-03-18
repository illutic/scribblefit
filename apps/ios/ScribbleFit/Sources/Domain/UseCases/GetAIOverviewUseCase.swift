import Foundation

@MainActor
public struct GetAIOverviewUseCase: Sendable {
    private let repository: InsightsRepository

    public init(repository: InsightsRepository) {
        self.repository = repository
    }

    public func execute() async throws -> AIOverview {
        try await repository.getAIOverview()
    }
}
