import Foundation

public enum WorkoutExercisesIntent: Sendable {
    case exerciseClicked(String)
    case refresh
    case navigateBack
}
