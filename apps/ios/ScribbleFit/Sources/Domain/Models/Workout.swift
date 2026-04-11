import Foundation

public struct Workout: Identifiable, Equatable, Sendable, Codable {
    public let id: UUID
    public let date: Date
    public let exercises: [Exercise]
    public let notes: [String]?

    public init(
        id: UUID = UUID(),
        date: Date,
        exercises: [Exercise],
        notes: [String]? = nil
    ) {
        self.id = id
        self.date = date
        self.exercises = exercises
        self.notes = notes
    }
}
