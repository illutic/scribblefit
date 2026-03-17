import Foundation

public struct Exercise: Identifiable, Equatable, Sendable {
    public let id: UUID
    public let name: String
    public let sets: [ExerciseSet]

    public init(id: UUID = UUID(), name: String, sets: [ExerciseSet]) {
        self.id = id
        self.name = name
        self.sets = sets
    }
}
