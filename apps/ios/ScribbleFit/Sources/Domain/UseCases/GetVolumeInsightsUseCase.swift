import Foundation

public struct GetVolumeInsightsUseCase: Sendable {
    private let repository: InsightsRepository

    public init(repository: InsightsRepository) {
        self.repository = repository
    }

    public func execute(startDate: Date, endDate: Date) -> AsyncStream<[VolumeDataPoint]> {
        repository.getVolumeInsights(startDate: startDate, endDate: endDate)
    }
}
