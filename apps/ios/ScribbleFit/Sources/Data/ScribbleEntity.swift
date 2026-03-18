import Foundation
import SwiftData

@Model
public final class ScribbleEntity {
    @Attribute(.unique) public var id: UUID
    public var rawText: String
    public var parsedJson: String?
    public var status: String
    public var createdAt: Date
    
    @Relationship(deleteRule: .cascade)
    public var exercises: [ExerciseEntity] = []
    
    public init(id: UUID, rawText: String, parsedJson: String?, status: String, createdAt: Date) {
        self.id = id
        self.rawText = rawText
        self.parsedJson = parsedJson
        self.status = status
        self.createdAt = createdAt
    }
}

@Model
public final class ExerciseEntity {
    @Attribute(.unique) public var id: UUID
    public var name: String
    public var muscleGroup: String
    
    @Relationship(deleteRule: .cascade)
    public var sets: [SetEntity] = []
    
    public var scribble: ScribbleEntity?
    
    public init(id: UUID, name: String, muscleGroup: String) {
        self.id = id
        self.name = name
        self.muscleGroup = muscleGroup
    }
}

@Model
public final class SetEntity {
    @Attribute(.unique) public var id: UUID
    public var reps: Int
    public var weight: Double
    public var unit: String
    
    public var exercise: ExerciseEntity?
    
    public init(id: UUID, reps: Int, weight: Double, unit: String) {
        self.id = id
        self.reps = reps
        self.weight = weight
        self.unit = unit
    }
}
