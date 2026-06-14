import Foundation
import Observation
import Combine
import CoreModel

@Observable
@MainActor
public final class InsightsStore {
    public var state = InsightsState()

    private let getAIOverviewUseCase: GetAIOverviewUseCase
    private let getVolumeInsightsUseCase: GetVolumeInsightsUseCase
    private let getFrequencyInsightsUseCase: GetFrequencyInsightsUseCase
    private let getMuscleDistributionInsightsUseCase: GetMuscleDistributionInsightsUseCase
    private let configRepository: ConfigRepository

    private var observationTask: Task<Void, Never>?
    private var aiTrigger = PassthroughSubject<Void, Never>()
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
        setupAiDebounce()
        observeInsights()
    }

    public func onIntent(_ intent: InsightsIntent) {
        switch intent {
        case .refresh:
            observeInsights()
        case .selectPeriod(let period):
            state.selectedPeriod = period
            observeInsights()
        }
    }

    private func setupAiDebounce() {
        aiTrigger
            .debounce(for: .seconds(2), scheduler: RunLoop.main)
            .sink { [weak self] _ in
                Task { [weak self] in
                    await self?.generateAIOverview()
                }
            }
            .store(in: &cancellables)
    }

    private func observeInsights() {
        observationTask?.cancel()
        state.isLoading = true
        state.errorMessage = nil

        let calendar = Calendar.current
        let endDate = Date()
        let startDate = calendar.date(byAdding: .day, value: -state.selectedPeriod.dayCount, to: endDate) ?? endDate

        observationTask = Task {
            // Volume Observation
            Task {
                let volumeStream = self.getVolumeInsightsUseCase.execute(startDate: startDate, endDate: endDate)
                for await volume in volumeStream {
                    if Task.isCancelled { break }
                    await MainActor.run { self.state.volumePoints = volume }
                }
            }

            // Frequency Observation (Primary loader trigger)
            Task {
                let frequencyStream = self.getFrequencyInsightsUseCase.execute(startDate: startDate, endDate: endDate)
                for await frequency in frequencyStream {
                    if Task.isCancelled { break }
                    await MainActor.run {
                        self.state.frequency = frequency
                        self.state.isLoading = false // Primary data received, hide main loader
                        if frequency.totalWorkouts >= 2 {
                            self.aiTrigger.send()
                        }
                    }
                }
            }

            // Distribution Observation
            Task {
                let distributionStream = self.getMuscleDistributionInsightsUseCase.execute(startDate: startDate, endDate: endDate)
                for await distribution in distributionStream {
                    if Task.isCancelled { break }
                    await MainActor.run { self.state.distribution = distribution }
                }
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
