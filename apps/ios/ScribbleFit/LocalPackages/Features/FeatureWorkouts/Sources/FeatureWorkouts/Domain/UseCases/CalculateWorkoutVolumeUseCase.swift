import Foundation
import CoreModel

public struct CalculateWorkoutVolumeUseCase: Sendable {
    public init() {}
    
    public func execute(workout: Workout) -> Double {
        workout.exercises.reduce(0.0) { workoutTotal, exercise in
            workoutTotal + exercise.sets.reduce(0.0) { exerciseTotal, set in
                exerciseTotal + Double((set.weight ?? 0) * Float(set.reps))
            }
        }
    }
}
