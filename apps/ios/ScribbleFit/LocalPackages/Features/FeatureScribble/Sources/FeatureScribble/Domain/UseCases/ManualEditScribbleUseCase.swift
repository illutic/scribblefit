import Foundation
import CoreModel

public struct ManualEditScribbleUseCase: Sendable {
    private let scribbleRepository: ScribbleRepository

    public init(scribbleRepository: ScribbleRepository) {
        self.scribbleRepository = scribbleRepository
    }

    @MainActor
    public func updateExerciseName(scribbleId: UUID, exerciseId: UUID, newName: String) async throws {
        guard let scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let updatedExercises = scribble.exercises.map { exercise in
            exercise.id == exerciseId ? exercise.copy(canonicalName: newName) : exercise
        }
        try await scribbleRepository.updateScribble(scribble.copy(exercises: updatedExercises))
    }

    @MainActor
    public func updateSetWeight(scribbleId: UUID, exerciseId: UUID, setId: UUID, newWeight: Float) async throws {
        guard let scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let updatedExercises = scribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                let updatedSets = exercise.sets.map { set in
                    set.id == setId ? set.copy(weight: newWeight) : set
                }
                return exercise.copy(sets: updatedSets)
            }
            return exercise
        }
        try await scribbleRepository.updateScribble(scribble.copy(exercises: updatedExercises))
    }

    @MainActor
    public func updateSetReps(scribbleId: UUID, exerciseId: UUID, setId: UUID, newReps: Int) async throws {
        guard let scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let updatedExercises = scribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                let updatedSets = exercise.sets.map { set in
                    set.id == setId ? set.copy(reps: newReps) : set
                }
                return exercise.copy(sets: updatedSets)
            }
            return exercise
        }
        try await scribbleRepository.updateScribble(scribble.copy(exercises: updatedExercises))
    }

    @MainActor
    public func deleteSet(scribbleId: UUID, exerciseId: UUID, setId: UUID) async throws {
        guard let scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let updatedExercises = scribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                let filteredSets = exercise.sets.filter { $0.id != setId }
                // Re-indexing sets
                let reorderedSets = filteredSets.enumerated().map { index, set in
                    set.copy(setNumber: index + 1)
                }
                return exercise.copy(sets: reorderedSets)
            }
            return exercise
        }
        try await scribbleRepository.updateScribble(scribble.copy(exercises: updatedExercises))
    }

    @MainActor
    public func deleteExercise(scribbleId: UUID, exerciseId: UUID) async throws {
        guard let scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let filteredExercises = scribble.exercises.filter { $0.id != exerciseId }

        if filteredExercises.isEmpty {
            try await scribbleRepository.deleteScribble(id: scribbleId)
        } else {
            try await scribbleRepository.updateScribble(scribble.copy(exercises: filteredExercises))
        }
    }

    @MainActor
    public func addSet(scribbleId: UUID, exerciseId: UUID) async throws {
        guard let scribble = try await scribbleRepository.getScribble(id: scribbleId) else { return }
        let updatedExercises = scribble.exercises.map { exercise in
            if exercise.id == exerciseId {
                let nextNumber = (exercise.sets.map { $0.setNumber }.max() ?? 0) + 1
                let newSet = ExerciseSet(
                    id: UUID(),
                    setNumber: nextNumber,
                    weight: Float(0.0),
                    reps: 0
                )
                return exercise.copy(sets: exercise.sets + [newSet])
            }
            return exercise
        }
        try await scribbleRepository.updateScribble(scribble.copy(exercises: updatedExercises))
    }
}
