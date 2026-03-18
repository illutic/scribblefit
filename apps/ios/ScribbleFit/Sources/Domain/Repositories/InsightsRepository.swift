import Foundation

@MainActor
public protocol InsightsRepository: Sendable {
    func getVolumeInsights(startDate: Date, endDate: Date) -> AsyncStream<[VolumeDataPoint]>
    func getFrequencyInsights() -> AsyncStream<FrequencyData>
    func getMuscleDistributionInsights() -> AsyncStream<[MuscleGroupDistribution]>
    func getAIOverview() async throws -> AIOverview
}
