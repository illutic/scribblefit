import Foundation

public struct GetFrequencyInsightsUseCase: Sendable {
    private let repository: InsightsRepository

    public init(repository: InsightsRepository) {
        self.repository = repository
    }

    public func execute() -> AsyncStream<FrequencyData> {
        repository.getFrequencyInsights()
    }
}
