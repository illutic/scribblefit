import Foundation
import CoreModel

public struct ExerciseDetailsState: Sendable {
    public var exerciseName: String
    public var isLoading: Bool = false
    public var details: ExerciseDetails?
    public var isGeneratingAI: Bool = false
    public var aiInsight: AIInsight?
    public var error: String?
    public var weightUnit: WeightUnit = .lbs
    public var showTrends: Bool = false
    public var showHistory: Bool = false
    public var shouldDismiss: Bool = false

    public init(exerciseName: String) {
        self.exerciseName = exerciseName
    }
}
