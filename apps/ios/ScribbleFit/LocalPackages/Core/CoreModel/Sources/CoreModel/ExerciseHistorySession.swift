import Foundation

public struct ExerciseHistorySession: Identifiable, Codable, Sendable, Equatable {
    public let id: UUID
    public let exercise: Exercise
    public let totalVolume: Float
    public let maxWeight: Float
    public let summary: String
    public let isPersonalBest: Bool
    public let scribbleId: UUID
    
    public var date: Date { exercise.createdAt }
    
    public init(
        id: UUID = UUID(),
        exercise: Exercise,
        totalVolume: Float,
        maxWeight: Float,
        summary: String,
        isPersonalBest: Bool,
        scribbleId: UUID
    ) {
        self.id = id
        self.exercise = exercise
        self.totalVolume = totalVolume
        self.maxWeight = maxWeight
        self.summary = summary
        self.isPersonalBest = isPersonalBest
        self.scribbleId = scribbleId
    }
}
