import Foundation

@MainActor
public struct GetMuscleDistributionInsightsUseCase: Sendable {
    private let repository: InsightsRepository

    public init(repository: InsightsRepository) {
        self.repository = repository
    }

    public func execute() -> AsyncStream<[MuscleGroupDistribution]> {
        repository.getMuscleDistributionInsights()
    }
}
