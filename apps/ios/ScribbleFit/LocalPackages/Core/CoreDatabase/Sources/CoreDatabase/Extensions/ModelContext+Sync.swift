import Foundation
import SwiftData
import CoreModel

public extension ModelContext {
    @MainActor
    func syncExercises(for domainExercises: [Exercise]) throws -> [ExerciseEntity] {
        var entities: [ExerciseEntity] = []
        
        for updated in domainExercises {
            let instanceId = updated.id
            let instancePredicate = #Predicate<ExerciseEntity> { $0.id == instanceId }
            var instanceDescriptor = FetchDescriptor<ExerciseEntity>(predicate: instancePredicate)
            instanceDescriptor.fetchLimit = 1
            
            let instance: ExerciseEntity
            if let existingInstance = try self.fetch(instanceDescriptor).first {
                instance = existingInstance
                instance.name = updated.canonicalName
                instance.muscleGroup = updated.muscleGroup
                instance.isDraft = updated.isDraft
                instance.estimated1RM = updated.estimated1RM
                instance.intensity = updated.intensity
                instance.improvement = updated.improvement
            } else {
                instance = updated.toEntity()
                self.insert(instance)
            }
            
            try syncSets(for: instance, with: updated.sets)
            entities.append(instance)
        }
        
        return entities
    }

    @MainActor
    func syncSets(for exercise: ExerciseEntity, with updatedSets: [ExerciseSet]) throws {
        let existingSets = exercise.sets
        var finalSets: [SetEntity] = []
        
        // Delete removed
        for existing in existingSets {
            if !updatedSets.contains(where: { $0.id == existing.id }) {
                self.delete(existing)
            }
        }
        
        // Update or Add
        for updated in updatedSets {
            if let existing = existingSets.first(where: { $0.id == updated.id }) {
                existing.setNumber = updated.setNumber
                existing.weight = updated.weight
                existing.reps = updated.reps
                existing.rpe = updated.rpe
                existing.notes = updated.notes
                finalSets.append(existing)
            } else {
                let newSet = updated.toEntity()
                self.insert(newSet)
                finalSets.append(newSet)
            }
        }
        exercise.sets = finalSets
    }
}
