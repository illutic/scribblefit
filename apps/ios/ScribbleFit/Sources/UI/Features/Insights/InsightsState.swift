import Foundation

public struct InsightsState: Sendable {
    public var isLoading: Bool = true
    public var volumePoints: [VolumeDataPoint] = []
    public var frequency: FrequencyData? = nil
    public var distribution: [MuscleGroupDistribution] = []
    public var errorMessage: String? = nil
    
    public init() {}
    
    public var isEmpty: Bool {
        !isLoading && (frequency == nil || frequency!.totalWorkouts < 2)
    }
}
