import Foundation
import CoreModel

public struct ExerciseDetailsState: Sendable {
    public var exerciseName: String
    public var isLoading: Bool = false
    public var details: ExerciseDetails? = nil
    public var isGeneratingAI: Bool = false
    public var aiInsight: AIInsight? = nil
    public var error: String? = nil
    public var weightUnit: WeightUnit = .lbs
    public var showTrends: Bool = false
    
    public init(exerciseName: String) {
        self.exerciseName = exerciseName
    }
}
