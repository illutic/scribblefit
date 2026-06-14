import Foundation

public struct ExerciseSet: Identifiable, Equatable, Sendable, Codable {
    public let id: UUID
    public var setNumber: Int
    public var weight: Float?
    public var reps: Int
    public var rpe: Float?
    public var notes: String?

    public init(
        id: UUID = UUID(),
        setNumber: Int,
        weight: Float?,
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

    public func copy(
        id: UUID? = nil,
        setNumber: Int? = nil,
        weight: Float? = nil,
        reps: Int? = nil,
        rpe: Float? = nil,
        notes: String? = nil
    ) -> ExerciseSet {
        return .init(
            id: id ?? self.id,
            setNumber: setNumber ?? self.setNumber,
            weight: weight ?? self.weight,
            reps: reps ?? self.reps,
            rpe: rpe ?? self.rpe,
            notes: notes ?? self.notes
        )
    }
}
