import Foundation
import CoreModel

public struct ManualEditScribbleUseCase: Sendable {
    private let scribbleRepository: ScribbleRepository
    
    public init(scribbleRepository: ScribbleRepository) {
        self.scribbleRepository = scribbleRepository
    }
    
    @MainActor
    public func updateExerciseName(scribbleId: UUID, exerciseId: UUID, newName: String) async throws {
        guard var scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let updatedExercises = scribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                return Exercise(
                    id: exercise.id,
                    canonicalName: newName,
                    muscleGroup: exercise.muscleGroup,
                    sets: exercise.sets,
                    isDraft: exercise.isDraft,
                    estimated1RM: exercise.estimated1RM,
                    intensity: exercise.intensity
                )
            }
            return exercise
        }
        scribble.exercises = updatedExercises
        try await scribbleRepository.updateScribble(scribble)
    }

    @MainActor
    public func updateSetWeight(scribbleId: UUID, exerciseId: UUID, setId: UUID, newWeight: Float) async throws {
        guard var scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let updatedExercises = scribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                let updatedSets = exercise.sets.map { set in
                    if set.id == setId {
                        return ExerciseSet(
                            id: set.id,
                            setNumber: set.setNumber,
                            weight: newWeight,
                            reps: set.reps,
                            rpe: set.rpe,
                            notes: set.notes
                        )
                    }
                    return set
                }
                return Exercise(
                    id: exercise.id,
                    canonicalName: exercise.canonicalName,
                    muscleGroup: exercise.muscleGroup,
                    sets: updatedSets,
                    isDraft: exercise.isDraft,
                    estimated1RM: exercise.estimated1RM,
                    intensity: exercise.intensity
                )
            }
            return exercise
        }
        scribble.exercises = updatedExercises
        try await scribbleRepository.updateScribble(scribble)
    }

    @MainActor
    public func updateSetReps(scribbleId: UUID, exerciseId: UUID, setId: UUID, newReps: Int) async throws {
        guard var scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let updatedExercises = scribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                let updatedSets = exercise.sets.map { set in
                    if set.id == setId {
                        return ExerciseSet(
                            id: set.id,
                            setNumber: set.setNumber,
                            weight: set.weight,
                            reps: newReps,
                            rpe: set.rpe,
                            notes: set.notes
                        )
                    }
                    return set
                }
                return Exercise(
                    id: exercise.id,
                    canonicalName: exercise.canonicalName,
                    muscleGroup: exercise.muscleGroup,
                    sets: updatedSets,
                    isDraft: exercise.isDraft,
                    estimated1RM: exercise.estimated1RM,
                    intensity: exercise.intensity
                )
            }
            return exercise
        }
        scribble.exercises = updatedExercises
        try await scribbleRepository.updateScribble(scribble)
    }

    @MainActor
    public func deleteSet(scribbleId: UUID, exerciseId: UUID, setId: UUID) async throws {
        guard var scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let updatedExercises = scribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                let filteredSets = exercise.sets.filter { $0.id != setId }
                // Re-indexing sets
                let reorderedSets = filteredSets.enumerated().map { index, set in
                    ExerciseSet(
                        id: set.id,
                        setNumber: index + 1,
                        weight: set.weight,
                        reps: set.reps,
                        rpe: set.rpe,
                        notes: set.notes
                    )
                }
                return Exercise(
                    id: exercise.id,
                    canonicalName: exercise.canonicalName,
                    muscleGroup: exercise.muscleGroup,
                    sets: reorderedSets,
                    isDraft: exercise.isDraft,
                    estimated1RM: exercise.estimated1RM,
                    intensity: exercise.intensity
                )
            }
            return exercise
        }
        scribble.exercises = updatedExercises
        try await scribbleRepository.updateScribble(scribble)
    }
}
