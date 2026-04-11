import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

public enum CanvasIntent: Sendable {
    // Canvas
    case updateScribbleText(String)
    case addScribble(String)
    case retryScribbleParsing(Scribble)
    case toggleInputExpansion
    case clickOnScribble(Scribble)
    case onPreviousDayClick
    case onNextDayClick
    case showDatePicker
    case dismissDatePicker
    case onDateSelected(Date)

    // Manual Editing
    case updateExerciseName(UUID, String)
    case updateSetWeight(UUID, UUID, String)
    case updateSetReps(UUID, UUID, String)

    // Scribble Dialog
    case confirmScribble(Scribble)
    case deleteScribble(UUID)
    case dismissScribbleDialog

    // Navigation
    case navigateToSettings
    case navigateToProfile
}
