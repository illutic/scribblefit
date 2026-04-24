import Foundation
import SwiftData
@preconcurrency import Combine
import CoreModel
import CoreDatabase

@MainActor
public final class ExerciseRepositoryImpl: ExerciseRepository {
    private let modelContainer: ModelContainer
    private let modelContext: ModelContext
    private let changeSubject = PassthroughSubject<Void, Never>()

    public init(modelContainer: ModelContainer) {
        self.modelContainer = modelContainer
        self.modelContext = modelContainer.mainContext
    }

    public func getExercises(query: String) async throws -> [Exercise] {
        let predicate = #Predicate<ExerciseEntity> { entity in
            query.isEmpty || entity.name.contains(query)
        }
        let descriptor = FetchDescriptor<ExerciseEntity>(predicate: predicate, sortBy: [SortDescriptor(\.name)])
        let entities = try modelContext.fetch(descriptor)
        return entities.map { $0.toDomain() }
    }

    public func getExercise(id: UUID) async throws -> Exercise? {
        let predicate = #Predicate<ExerciseEntity> { $0.id == id }
        var descriptor = FetchDescriptor<ExerciseEntity>(predicate: predicate)
        descriptor.fetchLimit = 1
        return try modelContext.fetch(descriptor).first?.toDomain()
    }

    public func saveExercise(_ exercise: Exercise) async throws {
        _ = try modelContext.syncExercises(for: [exercise])
        try modelContext.save()
        changeSubject.send()
    }

    public func saveExercise(_ exercise: Exercise, to scribbleId: UUID) async throws {
        let predicate = #Predicate<ScribbleEntity> { $0.id == scribbleId }
        var descriptor = FetchDescriptor<ScribbleEntity>(predicate: predicate)
        descriptor.fetchLimit = 1
        
        if let scribble = try modelContext.fetch(descriptor).first {
            let exerciseEntities = try modelContext.syncExercises(for: [exercise])
            if let newExercise = exerciseEntities.first {
                scribble.exercises.append(newExercise)
            }
            try modelContext.save()
            changeSubject.send()
        }
    }

    public func updateExercise(_ exercise: Exercise) async throws {
        let id = exercise.id
        let predicate = #Predicate<ExerciseEntity> { $0.id == id }
        var descriptor = FetchDescriptor<ExerciseEntity>(predicate: predicate)
        descriptor.fetchLimit = 1
        
        if let entity = try modelContext.fetch(descriptor).first {
            entity.estimated1RM = exercise.estimated1RM
            entity.intensity = exercise.intensity
            entity.improvement = exercise.improvement
            
            // Sync sets
            try modelContext.syncSets(for: entity, with: exercise.sets)
            
            try modelContext.save()
            changeSubject.send()
        }
    }

    public func deleteExercise(id: UUID) async throws {
        let predicate = #Predicate<ExerciseEntity> { $0.id == id }
        try modelContext.delete(model: ExerciseEntity.self, where: predicate)
        try modelContext.save()
        changeSubject.send()
    }
}
