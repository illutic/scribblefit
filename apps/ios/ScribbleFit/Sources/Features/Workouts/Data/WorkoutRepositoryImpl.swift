import Foundation
import SwiftData
@preconcurrency import Combine
#if SWIFT_PACKAGE
import CoreModel
import CoreDatabase
#endif

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
        
        if let existing = try modelContext.fetch(descriptor).first {
            existing.date = workout.date
            existing.notes = workout.notes?.joined(separator: "\n")
            
            // Clear existing exercises to prevent duplication
            for exercise in existing.exercises {
                modelContext.delete(exercise)
            }
            existing.exercises = workout.exercises.map { $0.toEntity() }
        } else {
            let entity = WorkoutEntity(
                id: workout.id,
                date: workout.date,
                notes: workout.notes?.joined(separator: "\n")
            )
            modelContext.insert(entity)
            entity.exercises = workout.exercises.map { $0.toEntity() }
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

    public func getWorkoutsInRange(startDate: Date, endDate: Date) async throws -> [Workout] {
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

        let entities = try modelContext.fetch(descriptor)
        return entities.map { $0.toDomain() }
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
