import Foundation
import SwiftData
@preconcurrency import Combine
import CoreModel
import CoreDatabase

@MainActor
public final class ScribbleRepositoryImpl: ScribbleRepository {
    private let modelContainer: ModelContainer
    private let modelContext: ModelContext
    private let changeSubject = PassthroughSubject<Void, Never>()

    public init(modelContainer: ModelContainer) {
        self.modelContainer = modelContainer
        self.modelContext = ModelContext(modelContainer)
    }

    public func getScribbles(for date: Date) -> AsyncStream<[Scribble]> {
        let (stream, continuation) = AsyncStream.makeStream(of: [Scribble].self)
        
        let cancellable = changeSubject
            .prepend(())
            .sink { [weak self] _ in
                guard let self = self else { return }
                Task { @MainActor in
                    do {
                        let calendar = Calendar.current
                        let startOfDay = calendar.startOfDay(for: date)
                        let endOfDay = calendar.date(byAdding: .day, value: 1, to: startOfDay)!
                        
                        let predicate = #Predicate<ScribbleEntity> { scribble in
                            scribble.createdAt >= startOfDay && scribble.createdAt < endOfDay
                        }
                        
                        let descriptor = FetchDescriptor<ScribbleEntity>(predicate: predicate, sortBy: [SortDescriptor(\.createdAt)])
                        
                        let entities = try self.modelContext.fetch(descriptor)
                        continuation.yield(entities.map { $0.toDomain() })
                    } catch {
                        continuation.yield([])
                    }
                }
            }
        
        continuation.onTermination = { [cancellable] _ in
            cancellable.cancel()
        }
        
        return stream
    }

    public func addScribble(_ scribble: Scribble) async throws {
        let entity = ScribbleEntity(
            id: scribble.id,
            rawText: scribble.rawText,
            status: scribble.status.rawValue,
            createdAt: scribble.createdAt,
            parsedJson: scribble.parsedJson,
            workoutId: scribble.workoutId
        )
        modelContext.insert(entity)
        entity.exercises = try syncExercises(for: scribble.exercises)
        try modelContext.save()
        changeSubject.send()
    }

    public func updateScribble(_ scribble: Scribble) async throws {
        let id = scribble.id
        let predicate = #Predicate<ScribbleEntity> { $0.id == id }
        var descriptor = FetchDescriptor<ScribbleEntity>(predicate: predicate)
        descriptor.fetchLimit = 1
        
        if let entity = try modelContext.fetch(descriptor).first {
            entity.rawText = scribble.rawText
            entity.status = scribble.status.rawValue
            entity.createdAt = scribble.createdAt
            entity.parsedJson = scribble.parsedJson
            entity.workoutId = scribble.workoutId
            
            // Sync exercises
            entity.exercises = try syncExercises(for: scribble.exercises)
            
            try modelContext.save()
            changeSubject.send()
        }
    }

    private func syncExercises(for domainExercises: [Exercise]) throws -> [ExerciseEntity] {
        var entities: [ExerciseEntity] = []
        
        for updated in domainExercises {
            let exerciseId = updated.id
            let predicate = #Predicate<ExerciseEntity> { $0.id == exerciseId }
            var descriptor = FetchDescriptor<ExerciseEntity>(predicate: predicate)
            descriptor.fetchLimit = 1
            
            if let existing = try modelContext.fetch(descriptor).first {
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
                modelContext.insert(newEntity)
                entities.append(newEntity)
            }
        }
        
        return entities
    }

    private func syncSets(for exercise: ExerciseEntity, with updatedSets: [ExerciseSet]) {
        let existingSets = exercise.sets
        var finalSets: [SetEntity] = []
        
        // Delete removed
        for existing in existingSets {
            if !updatedSets.contains(where: { $0.id == existing.id }) {
                modelContext.delete(existing)
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
                modelContext.insert(newSet)
                finalSets.append(newSet)
            }
        }
        exercise.sets = finalSets
    }

    public func deleteScribble(id: UUID) async throws {
        let predicate = #Predicate<ScribbleEntity> { $0.id == id }
        try modelContext.delete(model: ScribbleEntity.self, where: predicate)
        try modelContext.save()
        changeSubject.send()
    }

    public func getScribble(id: UUID) async throws -> Scribble? {
        let predicate = #Predicate<ScribbleEntity> { $0.id == id }
        var descriptor = FetchDescriptor<ScribbleEntity>(predicate: predicate)
        descriptor.fetchLimit = 1
        return try modelContext.fetch(descriptor).first?.toDomain()
    }

    public func clearScribbleExercises(scribbleId: UUID) async throws {
        let predicate = #Predicate<ScribbleEntity> { $0.id == scribbleId }
        var descriptor = FetchDescriptor<ScribbleEntity>(predicate: predicate)
        descriptor.fetchLimit = 1
        
        if let scribble = try modelContext.fetch(descriptor).first {
            // Only delete exercises if they are not linked to a workout
            for exercise in scribble.exercises {
                if exercise.workout == nil {
                    modelContext.delete(exercise)
                }
            }
            scribble.exercises = []
            try modelContext.save()
            changeSubject.send()
        }
    }
}
