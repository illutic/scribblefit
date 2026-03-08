import Foundation
import SwiftData

@Model public final class WorkoutSet {
    public var id: String
    public var weight: Double
    public var reps: Int
    public var rpe: Double?
    public var notes: String?
    public var exerciseId: String
    public var workout: WorkoutLog?

    public init(id: String, weight: Double, reps: Int, rpe: Double? = nil, notes: String? = nil, exerciseId: String) {
        self.id = id
        self.weight = weight
        self.reps = reps
        self.rpe = rpe
        self.notes = notes
        self.exerciseId = exerciseId
    }
}
