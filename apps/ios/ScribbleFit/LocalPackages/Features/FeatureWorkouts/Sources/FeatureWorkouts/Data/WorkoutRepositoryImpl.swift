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
        self.modelContext = modelContainer.mainContext
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
            existingWorkout.exercises = try modelContext.syncExercises(for: workout.exercises)
        } else {
            let entity = WorkoutEntity(
                id: workout.id,
                date: workout.date,
                notes: workout.notes?.joined(separator: "\n")
            )
            modelContext.insert(entity)
            entity.exercises = try modelContext.syncExercises(for: workout.exercises)
        }
        
        try modelContext.save()
        changeSubject.send()
    }

    public func deleteWorkout(id: UUID) async throws {
        let predicate = #Predicate<WorkoutEntity> { $0.id == id }
        try modelContext.delete(model: WorkoutEntity.self, where: predicate)
        try modelContext.save()
        changeSubject.send()
    }

    public func observeWorkout(id: UUID) -> AsyncStream<Workout?> {
        let (stream, continuation) = AsyncStream.makeStream(of: Workout?.self)
        
        let cancellable = changeSubject
            .prepend(())
            .sink { [weak self] _ in
                guard let self = self else { return }
                Task { @MainActor in
                    do {
                        let predicate = #Predicate<WorkoutEntity> { $0.id == id }
                        var descriptor = FetchDescriptor<WorkoutEntity>(predicate: predicate)
                        descriptor.fetchLimit = 1
                        let entity = try self.modelContext.fetch(descriptor).first
                        continuation.yield(entity?.toDomain())
                    } catch {
                        continuation.yield(nil)
                    }
                }
            }
        
        continuation.onTermination = { [cancellable] _ in
            cancellable.cancel()
        }
        
        return stream
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

    public func getWorkoutsWithExercise(exerciseName: String) -> AsyncStream<[Workout]> {
        let (stream, continuation) = AsyncStream.makeStream(of: [Workout].self)
        
        let cancellable = changeSubject
            .prepend(())
            .sink { [weak self] _ in
                guard let self = self else { return }
                Task { @MainActor in
                    do {
                        // Observe all to catch any changes to exercises in any workout
                        let descriptor = FetchDescriptor<WorkoutEntity>(sortBy: [SortDescriptor(\.date, order: .reverse)])
                        let entities = try self.modelContext.fetch(descriptor)
                        let workouts = entities.map { $0.toDomain() }
                        let filtered = workouts.filter { workout in
                            workout.exercises.contains { $0.canonicalName == exerciseName }
                        }
                        continuation.yield(filtered)
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
