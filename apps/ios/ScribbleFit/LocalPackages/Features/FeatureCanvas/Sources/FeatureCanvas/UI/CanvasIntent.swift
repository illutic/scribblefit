import Foundation
import CoreModel
import CoreCommon

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
    case deleteSet(UUID, UUID) // exerciseId, setId
    case addSet(UUID)
    case deleteExercise(UUID)

    // Manual Entry
    case showAddExerciseSheet
    case hideAddExerciseSheet
    case saveManualExercise(name: String, muscleGroup: String, sets: [ExerciseSet])

    // Scribble Dialog
    case confirmScribble(Scribble)
    case deleteScribble(UUID)
    case dismissScribbleDialog

    // Navigation
    case navigateToSettings
    case dismissSettings
    case navigateToExerciseDetails(String)
    case navigateToScribbleDetails(UUID)
    case dismissDetails
}
