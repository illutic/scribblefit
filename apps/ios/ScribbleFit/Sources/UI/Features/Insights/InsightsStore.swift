import Foundation
import Observation

@Observable
@MainActor
public final class InsightsStore {
    public private(set) var state = InsightsState()
    
    private let getVolumeInsights: GetVolumeInsightsUseCase
    private let getFrequencyInsights: GetFrequencyInsightsUseCase
    private let getMuscleDistributionInsights: GetMuscleDistributionInsightsUseCase
    
    public init(
        getVolumeInsights: GetVolumeInsightsUseCase,
        getFrequencyInsights: GetFrequencyInsightsUseCase,
        getMuscleDistributionInsights: GetMuscleDistributionInsightsUseCase
    ) {
        self.getVolumeInsights = getVolumeInsights
        self.getFrequencyInsights = getFrequencyInsights
        self.getMuscleDistributionInsights = getMuscleDistributionInsights
        
        loadInsights()
    }
    
    public func onIntent(_ intent: InsightsIntent) {
        switch intent {
        case .refresh:
            loadInsights()
        }
    }
    
    private func loadInsights() {
        state.isLoading = true
        
        let endDate = Date()
        let startDate = Calendar.current.date(byAdding: .month, value: -1, to: endDate)!
        
        Task {
            for await volume in getVolumeInsights.execute(startDate: startDate, endDate: endDate) {
                state.volumePoints = volume
            }
        }
        
        Task {
            for await frequency in getFrequencyInsights.execute() {
                state.frequency = frequency
                state.isLoading = false
            }
        }
        
        Task {
            for await distribution in getMuscleDistributionInsights.execute() {
                state.distribution = distribution
            }
        }
    }
}
