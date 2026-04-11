import Foundation

public struct ExerciseSet: Identifiable, Equatable, Sendable, Codable {
    public let id: UUID
    public let setNumber: Int
    public let weight: Float
    public let reps: Int
    public let rpe: Float?
    public let notes: String?

    public init(
        id: UUID = UUID(),
        setNumber: Int,
        weight: Float,
        reps: Int,
        rpe: Float? = nil,
        notes: String? = nil
    ) {
        self.id = id
        self.setNumber = setNumber
        self.weight = weight
        self.reps = reps
        self.rpe = rpe
        self.notes = notes
    }
}
