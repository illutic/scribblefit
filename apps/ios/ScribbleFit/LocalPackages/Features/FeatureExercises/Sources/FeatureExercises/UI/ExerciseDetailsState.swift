import Foundation
import CoreModel

public struct ExerciseDetailsState: Sendable {
    public var exerciseName: String
    public var isLoading: Bool = false
    public var details: ExerciseDetails? = nil
    public var isGeneratingAI: Bool = false
    public var aiInsight: ExercisePerformanceInsight? = nil
    public var error: String? = nil
    public var weightUnit: WeightUnit = .lbs
    
    public init(exerciseName: String) {
        self.exerciseName = exerciseName
    }
}
