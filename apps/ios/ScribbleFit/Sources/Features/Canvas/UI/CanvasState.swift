import Foundation
import CoreModel

public struct CanvasState: Equatable, Sendable {
    public var isLoading: Bool = false
    public var currentDate: Date = Date()
    public var error: String? = nil
    public var currentScribbleText: String = ""
    public var scribbles: [Scribble] = []
    public var selectedScribble: Scribble? = nil
    public var aiInsights: [AIInsight] = []
    public var isGeneratingInsights: Bool = false
    public var isDatePickerVisible: Bool = false
    public var weightUnit: WeightUnit = .kgs
    public var isInputExpanded: Bool = false
    public var isSettingsVisible: Bool = false

    public init() {}

    public var isCurrentDate: Bool {
        Calendar.current.isDateInToday(currentDate)
    }

    public var dateString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "EEEE, MMMM d"
        return formatter.string(from: currentDate)
    }

    // Pre-resolved strings matching Android resources
    public let emptyScribbleText = String(localized: "What did you lift today?")
    public let textfieldPlaceholder = String(localized: "Enter workout (e.g., Bench 100kg 3x5)")
    public let appName = String(localized: "ScribbleFit")
    public let aiInsightsLabel = String(localized: "AI Insights")
    public let parsingWorkoutText = String(localized: "Parsing workout data...")
    public let tapToConfirmText = String(localized: "Tap to confirm")
    public let loggedLabel = String(localized: "LOGGED")
    public let estimated1rmLabel = String(localized: "Est. 1RM")
    public let intensityLabel = String(localized: "Intensity")
    public let failedToParseText = String(localized: "Failed to parse workout")
    public let retryLabel = String(localized: "Retry")
    public let removeLabel = String(localized: "Remove")
    
    public var weightUnitLabel: String {
        weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs")
    }
}
