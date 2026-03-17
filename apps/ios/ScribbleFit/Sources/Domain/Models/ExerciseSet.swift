import Foundation

public struct ExerciseSet: Identifiable, Equatable, Sendable {
    public let id: UUID
    public let reps: Int
    public let weight: Double
    public let unit: String

    public init(id: UUID = UUID(), reps: Int, weight: Double, unit: String) {
        self.id = id
        self.reps = reps
        self.weight = weight
        self.unit = unit
    }
}
