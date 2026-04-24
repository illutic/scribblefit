import Foundation
import CoreModel

@MainActor
public extension ExerciseEntity {
    func toDomain() -> Exercise {
        Exercise(
            id: id,
            canonicalName: name,
            muscleGroup: muscleGroup,
            sets: sets.map { $0.toDomain() }.sorted(by: { $0.setNumber < $1.setNumber }),
            isDraft: isDraft,
            createdAt: createdAt,
            estimated1RM: estimated1RM,
            intensity: intensity,
            improvement: improvement
        )
    }
}

@MainActor
public extension SetEntity {
    func toDomain() -> ExerciseSet {
        ExerciseSet(
            id: id,
            setNumber: setNumber,
            weight: weight,
            reps: reps,
            rpe: rpe,
            notes: notes
        )
    }
}

@MainActor
public extension ScribbleEntity {
    func toDomain() -> Scribble {
        Scribble(
            id: id,
            rawText: rawText,
            status: ScribbleStatus(rawValue: status.uppercased()) ?? .failed,
            createdAt: createdAt,
            parsedJson: parsedJson,
            exercises: exercises.map { $0.toDomain() }
        )
    }
}

@MainActor
public extension Scribble {
    func toEntity() -> ScribbleEntity {
        let entity = ScribbleEntity(
            id: id,
            rawText: rawText,
            status: status.rawValue,
            createdAt: createdAt,
            parsedJson: parsedJson
        )
        return entity
    }
}

@MainActor
public extension Exercise {
    func toEntity(createdAt: Date? = nil) -> ExerciseEntity {
        let entity = ExerciseEntity(
            id: id,
            name: canonicalName,
            muscleGroup: muscleGroup,
            createdAt: createdAt ?? self.createdAt,
            isDraft: isDraft,
            estimated1RM: estimated1RM,
            intensity: intensity,
            improvement: improvement
        )
        entity.sets = sets.map { $0.toEntity() }
        return entity
    }
}

@MainActor
public extension ExerciseSet {
    func toEntity() -> SetEntity {
        SetEntity(
            id: id,
            setNumber: setNumber,
            weight: weight,
            reps: reps,
            rpe: rpe,
            notes: notes
        )
    }
}
