import Foundation
import SwiftData
import CoreModel

public extension ModelContext {
    @MainActor
    func syncExercises(for domainExercises: [Exercise]) throws -> [ExerciseEntity] {
        var entities: [ExerciseEntity] = []
        
        for updated in domainExercises {
            let exerciseId = updated.id
            let predicate = #Predicate<ExerciseEntity> { $0.id == exerciseId }
            var descriptor = FetchDescriptor<ExerciseEntity>(predicate: predicate)
            descriptor.fetchLimit = 1
            
            if let existing = try self.fetch(descriptor).first {
                // Update existing
                existing.name = updated.canonicalName
                existing.muscleGroup = updated.muscleGroup
                existing.isDraft = updated.isDraft
                existing.estimated1RM = updated.estimated1RM
                existing.intensity = updated.intensity
                
                syncSets(for: existing, with: updated.sets)
                entities.append(existing)
            } else {
                // Create new
                let newEntity = updated.toEntity()
                self.insert(newEntity)
                entities.append(newEntity)
            }
        }
        
        return entities
    }

    @MainActor
    func syncSets(for exercise: ExerciseEntity, with updatedSets: [ExerciseSet]) {
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
