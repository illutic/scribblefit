import Foundation
import Observation

@Observable
@MainActor
public final class InsightsStore {
    public private(set) var state = InsightsState()
    
    private let getVolumeInsights: GetVolumeInsightsUseCase
    private let getFrequencyInsights: GetFrequencyInsightsUseCase
    private let getMuscleDistributionInsights: GetMuscleDistributionInsightsUseCase
    private let getAIOverview: GetAIOverviewUseCase
    
    private var volumeTask: Task<Void, Never>?
    private var frequencyTask: Task<Void, Never>?
    private var distributionTask: Task<Void, Never>?
    private var aiTask: Task<Void, Never>?
    
    public init(
        getVolumeInsights: GetVolumeInsightsUseCase,
        getFrequencyInsights: GetFrequencyInsightsUseCase,
        getMuscleDistributionInsights: GetMuscleDistributionInsightsUseCase,
        getAIOverview: GetAIOverviewUseCase
    ) {
        self.getVolumeInsights = getVolumeInsights
        self.getFrequencyInsights = getFrequencyInsights
        self.getMuscleDistributionInsights = getMuscleDistributionInsights
        self.getAIOverview = getAIOverview
        
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
        
        volumeTask?.cancel()
        volumeTask = Task {
            for await volume in getVolumeInsights.execute(startDate: startDate, endDate: endDate) {
                state.volumePoints = volume
            }
        }
        
        frequencyTask?.cancel()
        frequencyTask = Task {
            for await frequency in getFrequencyInsights.execute() {
                state.frequency = frequency
                state.isLoading = false
                
                if frequency.totalWorkouts >= 2 {
                    loadAIOverview()
                }
            }
        }
        
        distributionTask?.cancel()
        distributionTask = Task {
            for await distribution in getMuscleDistributionInsights.execute() {
                state.distribution = distribution
            }
        }
    }
    
    private func loadAIOverview() {
        guard !state.isGeneratingAI else { return }
        
        aiTask?.cancel()
        aiTask = Task {
            state.isGeneratingAI = true
            do {
                state.aiOverview = try await getAIOverview.execute()
            } catch {
                state.errorMessage = error.localizedDescription
            }
            state.isGeneratingAI = false
        }
    }
}
