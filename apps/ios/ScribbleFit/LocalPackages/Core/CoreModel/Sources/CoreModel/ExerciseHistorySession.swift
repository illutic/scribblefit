import Foundation

public struct ExerciseHistorySession: Identifiable, Codable, Sendable {
    public var id: UUID { exercise.id }
    public let workoutId: UUID
    public let date: Date
    public let exercise: Exercise
    
    public init(workoutId: UUID, date: Date, exercise: Exercise) {
        self.workoutId = workoutId
        self.date = date
        self.exercise = exercise
    }
}
