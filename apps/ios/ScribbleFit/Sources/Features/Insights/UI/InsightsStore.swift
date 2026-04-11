import Foundation
import Observation
import Combine
#if SWIFT_PACKAGE
import CoreModel
#endif

@Observable
@MainActor
public final class InsightsStore {
    public var state = InsightsState()

    private let getAIOverviewUseCase: GetAIOverviewUseCase
    private let getVolumeInsightsUseCase: GetVolumeInsightsUseCase
    private let getFrequencyInsightsUseCase: GetFrequencyInsightsUseCase
    private let getMuscleDistributionInsightsUseCase: GetMuscleDistributionInsightsUseCase
    private let configRepository: ConfigRepository

    private var loadTask: Task<Void, Never>?
    private var cancellables = Set<AnyCancellable>()

    public init(
        getAIOverviewUseCase: GetAIOverviewUseCase,
        getVolumeInsightsUseCase: GetVolumeInsightsUseCase,
        getFrequencyInsightsUseCase: GetFrequencyInsightsUseCase,
        getMuscleDistributionInsightsUseCase: GetMuscleDistributionInsightsUseCase,
        configRepository: ConfigRepository
    ) {
        self.getAIOverviewUseCase = getAIOverviewUseCase
        self.getVolumeInsightsUseCase = getVolumeInsightsUseCase
        self.getFrequencyInsightsUseCase = getFrequencyInsightsUseCase
        self.getMuscleDistributionInsightsUseCase = getMuscleDistributionInsightsUseCase
        self.configRepository = configRepository

        setupConfigObservation()
        loadInsights()
    }

    public func onIntent(_ intent: InsightsIntent) {
        switch intent {
        case .refresh:
            loadInsights()
        case .selectPeriod(let period):
            state.selectedPeriod = period
            loadInsights()
        }
    }

    private func loadInsights() {
        loadTask?.cancel()
        state.isLoading = true
        state.errorMessage = nil

        loadTask = Task {
            let calendar = Calendar.current
            let endDate = Date()
            let startDate = calendar.date(byAdding: .day, value: -state.selectedPeriod.dayCount, to: endDate) ?? endDate

            // Load volume, frequency, and distribution in parallel
            async let volumeResult = getVolumeInsightsUseCase.execute(startDate: startDate, endDate: endDate)
            async let frequencyResult = getFrequencyInsightsUseCase.execute(startDate: startDate, endDate: endDate)
            async let distributionResult = getMuscleDistributionInsightsUseCase.execute(startDate: startDate, endDate: endDate)

            do {
                let volume = try await volumeResult
                let frequency = try await frequencyResult
                let distribution = try await distributionResult

                if Task.isCancelled { return }

                state.volumePoints = volume
                state.frequency = frequency
                state.distribution = distribution
                state.isLoading = false

                // Only generate AI overview if there is enough data
                if frequency.totalWorkouts >= 2 {
                    await generateAIOverview()
                }
            } catch {
                if Task.isCancelled { return }
                state.errorMessage = error.localizedDescription
                state.isLoading = false
            }
        }
    }

    private func generateAIOverview() async {
        state.isGeneratingAI = true
        defer {
            if !Task.isCancelled {
                state.isGeneratingAI = false
            }
        }

        do {
            let insights = try await getAIOverviewUseCase.execute(date: Date())
            if !Task.isCancelled {
                state.aiOverview = AIOverview(insights: insights)
            }
        } catch {
            // Silent failure for AI overview; the stats are still visible
            print("Failed to generate AI overview: \(error)")
        }
    }

    private func setupConfigObservation() {
        state.weightUnit = configRepository.getConfig().weightUnit

        configRepository.configPublisher
            .receive(on: RunLoop.main)
            .sink { [weak self] config in
                self?.state.weightUnit = config.weightUnit
            }
            .store(in: &cancellables)
    }
}
