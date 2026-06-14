import Foundation
import CoreModel

public struct ExerciseTrendsState: Sendable {
    public var exerciseName: String
    public var isLoading: Bool = false

    // OneRM Data
    public var oneRMDataPoints: [TrendDataPoint] = []
    public var oneRMInsights: TrendInsights?

    // Volume Data
    public var volumeDataPoints: [TrendDataPoint] = []
    public var volumeInsights: TrendInsights?

    public var error: String?
    public var weightUnit: WeightUnit = .lbs
    public var selectedPeriod: TrendPeriod = .threeMonths

    public init(exerciseName: String) {
        self.exerciseName = exerciseName
    }

    // Resolved Strings
    public var navigationTitle: String { exerciseName }
    public var oneRMSectionTitle: String { String(localized: "Estimated 1RM") }
    public var volumeSectionTitle: String { String(localized: "Total Volume") }
    public var emptyDataMessage: String { String(localized: "Not enough data for trends yet") }
    public var periodPickerLabel: String { String(localized: "Period") }
    public var personalBestLabel: String { String(localized: "PB") }

    public func badgeText(for direction: TrendDirection, percentage: Float) -> String {
        switch direction {
        case .improving: return "+\(Int(percentage))%"
        case .stable: return String(localized: "STABLE")
        case .plateaued: return String(localized: "PLATEAUED")
        case .declining: return "\(Int(percentage))%"
        }
    }

    public var weightUnitLabel: String {
        weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs")
    }
}
