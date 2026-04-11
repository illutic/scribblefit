import Foundation
import SwiftData
#if SWIFT_PACKAGE
import CoreModel
#endif

@Model
public final class ScribbleEntity {
    @Attribute(.unique) public var id: UUID
    public var rawText: String
    public var status: String
    public var createdAt: Date
    public var parsedJson: String?
    
    @Relationship(deleteRule: .cascade, inverse: \ExerciseEntity.scribble)
    public var exercises: [ExerciseEntity] = []
    
    public init(id: UUID, rawText: String, status: String, createdAt: Date, parsedJson: String? = nil) {
        self.id = id
        self.rawText = rawText
        self.status = status
        self.createdAt = createdAt
        self.parsedJson = parsedJson
    }
}

@Model
public final class ExerciseEntity {
    @Attribute(.unique) public var id: UUID
    public var name: String
    public var muscleGroup: String
    public var isDraft: Bool
    public var estimated1RM: Float?
    public var intensity: Float?
    public var improvement: Float?
    
    public var scribble: ScribbleEntity?
    public var workout: WorkoutEntity?
    
    @Relationship(deleteRule: .cascade, inverse: \SetEntity.exercise)
    public var sets: [SetEntity] = []
    
    public init(
        id: UUID, 
        name: String, 
        muscleGroup: String, 
        isDraft: Bool = false,
        estimated1RM: Float? = nil,
        intensity: Float? = nil,
        improvement: Float? = nil
    ) {
        self.id = id
        self.name = name
        self.muscleGroup = muscleGroup
        self.isDraft = isDraft
        self.estimated1RM = estimated1RM
        self.intensity = intensity
        self.improvement = improvement
    }
}

@Model
public final class SetEntity {
    @Attribute(.unique) public var id: UUID
    public var setNumber: Int
    public var weight: Float
    public var reps: Int
    public var rpe: Float?
    public var notes: String?
    
    public var exercise: ExerciseEntity?
    
    public init(id: UUID, setNumber: Int, weight: Float, reps: Int, rpe: Float? = nil, notes: String? = nil) {
        self.id = id
        self.setNumber = setNumber
        self.weight = weight
        self.reps = reps
        self.rpe = rpe
        self.notes = notes
    }
}

@Model
public final class WorkoutEntity {
    @Attribute(.unique) public var id: UUID
    public var date: Date
    public var notes: String? // Store as newline separated string
    
    @Relationship(deleteRule: .nullify, inverse: \ExerciseEntity.workout)
    public var exercises: [ExerciseEntity] = []
    
    public init(id: UUID, date: Date, notes: String? = nil) {
        self.id = id
        self.date = date
        self.notes = notes
    }
}
