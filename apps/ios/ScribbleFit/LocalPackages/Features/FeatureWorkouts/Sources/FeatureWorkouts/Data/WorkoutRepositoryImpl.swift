import Foundation
import SwiftData
@preconcurrency import Combine
import CoreModel
import CoreDatabase

@MainActor
public final class WorkoutRepositoryImpl: WorkoutRepository {
    private let modelContainer: ModelContainer
    private let modelContext: ModelContext
    private let changeSubject = PassthroughSubject<Void, Never>()

    public init(modelContainer: ModelContainer) {
        self.modelContainer = modelContainer
        self.modelContext = ModelContext(modelContainer)
    }

    public func getWorkout(id: UUID) async throws -> Workout? {
        let predicate = #Predicate<WorkoutEntity> { $0.id == id }
        var descriptor = FetchDescriptor<WorkoutEntity>(predicate: predicate)
        descriptor.fetchLimit = 1
        return try modelContext.fetch(descriptor).first?.toDomain()
    }

    public func saveWorkout(_ workout: Workout) async throws {
        // Check if exists
        let id = workout.id
        let predicate = #Predicate<WorkoutEntity> { $0.id == id }
        var descriptor = FetchDescriptor<WorkoutEntity>(predicate: predicate)
        descriptor.fetchLimit = 1
        
        if let existingWorkout = try modelContext.fetch(descriptor).first {
            existingWorkout.date = workout.date
            existingWorkout.notes = workout.notes?.joined(separator: "\n")
            
            // Sync exercises
            existingWorkout.exercises = try syncExercises(for: workout.exercises)
        } else {
            let entity = WorkoutEntity(
                id: workout.id,
                date: workout.date,
                notes: workout.notes?.joined(separator: "\n")
            )
            modelContext.insert(entity)
            entity.exercises = try syncExercises(for: workout.exercises)
        }
        
        try modelContext.save()
        changeSubject.send()
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

    public func deleteWorkout(id: UUID) async throws {
        let predicate = #Predicate<WorkoutEntity> { $0.id == id }
        try modelContext.delete(model: WorkoutEntity.self, where: predicate)
        try modelContext.save()
        changeSubject.send()
    }

    public func getWorkoutsInRange(startDate: Date, endDate: Date) -> AsyncStream<[Workout]> {
        let (stream, continuation) = AsyncStream.makeStream(of: [Workout].self)
        
        let cancellable = changeSubject
            .prepend(())
            .sink { [weak self] _ in
                guard let self = self else { return }
                Task { @MainActor in
                    do {
                        let calendar = Calendar.current
                        let rangeStart = calendar.startOfDay(for: startDate)
                        let rangeEnd = calendar.date(byAdding: .day, value: 1, to: calendar.startOfDay(for: endDate))!

                        let predicate = #Predicate<WorkoutEntity> { workout in
                            workout.date >= rangeStart && workout.date < rangeEnd
                        }

                        let descriptor = FetchDescriptor<WorkoutEntity>(
                            predicate: predicate,
                            sortBy: [SortDescriptor(\.date)]
                        )

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

    public func getWorkouts(for date: Date) -> AsyncStream<[Workout]> {
        let (stream, continuation) = AsyncStream.makeStream(of: [Workout].self)
        
        let cancellable = changeSubject
            .prepend(())
            .sink { [weak self] _ in
                guard let self = self else { return }
                Task { @MainActor in
                    do {
                        let calendar = Calendar.current
                        let startOfDay = calendar.startOfDay(for: date)
                        let endOfDay = calendar.date(byAdding: .day, value: 1, to: startOfDay)!
                        
                        let predicate = #Predicate<WorkoutEntity> { workout in
                            workout.date >= startOfDay && workout.date < endOfDay
                        }
                        
                        let descriptor = FetchDescriptor<WorkoutEntity>(predicate: predicate, sortBy: [SortDescriptor(\.date)])
                        
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
}
