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
    public var isAddExerciseSheetVisible: Bool = false
    public var navigationState: NavigationState? = nil

    public enum NavigationState: Equatable, Sendable, Identifiable {
        case exerciseDetails(String)
        case scribbleDetails(UUID)

        public var id: String {
            switch self {
            case .exerciseDetails(let name):
                return "exercise-\(name)"
            case .scribbleDetails(let uuid):
                return "scribble-\(uuid.uuidString)"
            }
        }
    }

    public init() {}

    public func copy(
        isLoading: Bool? = nil,
        currentDate: Date? = nil,
        error: String? = nil,
        currentScribbleText: String? = nil,
        scribbles: [Scribble]? = nil,
        selectedScribble: Scribble?? = nil,
        aiInsights: [AIInsight]? = nil,
        isGeneratingInsights: Bool? = nil,
        isDatePickerVisible: Bool? = nil,
        weightUnit: WeightUnit? = nil,
        isInputExpanded: Bool? = nil,
        isSettingsVisible: Bool? = nil,
        isAddExerciseSheetVisible: Bool? = nil,
        navigationState: NavigationState?? = nil
    ) -> CanvasState {
        var newState = self
        if let isLoading = isLoading { newState.isLoading = isLoading }
        if let currentDate = currentDate { newState.currentDate = currentDate }
        if let error = error { newState.error = error }
        if let currentScribbleText = currentScribbleText { newState.currentScribbleText = currentScribbleText }
        if let scribbles = scribbles { newState.scribbles = scribbles }
        if let selectedScribble = selectedScribble { newState.selectedScribble = selectedScribble }
        if let aiInsights = aiInsights { newState.aiInsights = aiInsights }
        if let isGeneratingInsights = isGeneratingInsights { newState.isGeneratingInsights = isGeneratingInsights }
        if let isDatePickerVisible = isDatePickerVisible { newState.isDatePickerVisible = isDatePickerVisible }
        if let weightUnit = weightUnit { newState.weightUnit = weightUnit }
        if let isInputExpanded = isInputExpanded { newState.isInputExpanded = isInputExpanded }
        if let isSettingsVisible = isSettingsVisible { newState.isSettingsVisible = isSettingsVisible }
        if let isAddExerciseSheetVisible = isAddExerciseSheetVisible { newState.isAddExerciseSheetVisible = isAddExerciseSheetVisible }
        if let navigationState = navigationState { newState.navigationState = navigationState }
        return newState
    }

    public var isCurrentDate: Bool {
        Calendar.current.isDateInToday(currentDate)
    }

    private static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "EEEE, MMMM d"
        return formatter
    }()

    public var dateString: String {
        return Self.dateFormatter.string(from: currentDate)
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
