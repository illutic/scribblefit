import Foundation
import CoreModel

public enum InsightsPeriod: String, CaseIterable, Sendable {
    case daily
    case weekly
    case monthly

    public var label: String {
        switch self {
        case .daily: return String(localized: "Daily")
        case .weekly: return String(localized: "Weekly")
        case .monthly: return String(localized: "Monthly")
        }
    }

    public var dayCount: Int {
        switch self {
        case .daily: return 1
        case .weekly: return 7
        case .monthly: return 30
        }
    }
}

public struct InsightsState: Equatable, Sendable {
    public var isLoading: Bool = true
    public var isGeneratingAI: Bool = false
    public var volumePoints: [VolumeDataPoint] = []
    public var frequency: FrequencyData?
    public var distribution: [MuscleGroupDistribution] = []
    public var aiOverview: AIOverview?
    public var selectedPeriod: InsightsPeriod = .weekly
    public var errorMessage: String?
    public var weightUnit: WeightUnit = .kgs

    public init() {}

    public var isEmpty: Bool {
        !isLoading && (frequency == nil || frequency!.totalWorkouts < 2)
    }

    public var totalVolume: Float {
        volumePoints.reduce(0) { $0 + $1.volume }
    }

    public var totalExercises: Int {
        frequency?.totalExercises ?? 0
    }

    public var weightUnitLabel: String {
        weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs")
    }

    // Pre-resolved localized strings
    public let titleText = String(localized: "Insights")
    public let loadingTitleText = String(localized: "Analyzing your recent sessions...")
    public let loadingSubtitleText = String(localized: "Updating now")
    public let emptyTitleText = String(localized: "Log at least 3 workouts to unlock your first insight")
    public let emptyStatusText = String(localized: "No data yet")
    public let thisWeekText = String(localized: "This Week")
    public let exercisesText = String(localized: "Exercises")
    public let nothingToShowText = String(localized: "Nothing to show yet")
    public let sessionsLabel = String(localized: "Sessions")
    public let volumeLabel = String(localized: "Volume")
    public let exerciseCountLabel = String(localized: "Exercises")
    public let muscleDistributionLabel = String(localized: "Muscle Distribution")
    public let updatedJustNowText = String(localized: "Updated just now")
    public let weeklyVolumeLabel = String(localized: "Weekly Volume")
    public let perWeekLabel = String(localized: "/week")
}
