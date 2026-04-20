import Foundation
import CoreModel

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
public extension ExerciseEntity {
    func toDomain() -> Exercise {
        Exercise(
            id: id,
            canonicalName: name,
            muscleGroup: muscleGroup,
            sets: sets.map { $0.toDomain() }.sorted(by: { $0.setNumber < $1.setNumber }),
            isDraft: isDraft,
            estimated1RM: estimated1RM,
            intensity: intensity
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
            workoutId: workoutId,
            exercises: exercises.map { $0.toDomain() }
        )
    }
}

@MainActor
public extension WorkoutEntity {
    func toDomain() -> Workout {
        Workout(
            id: id,
            date: date,
            exercises: exercises.map { $0.toDomain() },
            notes: notes?.components(separatedBy: "\n")
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
            parsedJson: parsedJson,
            workoutId: workoutId
        )
        entity.exercises = exercises.map { $0.toEntity() }
        return entity
    }
}

@MainActor
public extension Exercise {
    func toEntity() -> ExerciseEntity {
        let entity = ExerciseEntity(
            id: id,
            name: canonicalName,
            muscleGroup: muscleGroup,
            isDraft: isDraft,
            estimated1RM: estimated1RM,
            intensity: intensity
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
