import Foundation

public struct InsightsState: Sendable {
    public var isLoading: Bool = true
    public var isGeneratingAI: Bool = false
    public var volumePoints: [VolumeDataPoint] = []
    public var frequency: FrequencyData? = nil
    public var distribution: [MuscleGroupDistribution] = []
    public var aiOverview: AIOverview? = nil
    public var errorMessage: String? = nil
    
    public init() {}
    
    public var isEmpty: Bool {
        !isLoading && (frequency == nil || frequency!.totalWorkouts < 2)
    }
}
