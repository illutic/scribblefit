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
    private var observers: Set<AnyCancellable> = []

    public init(modelContainer: ModelContainer) {
        self.modelContainer = modelContainer
        self.modelContext = modelContainer.mainContext

        NotificationCenter.default.publisher(for: ModelContext.didSave)
            .receive(on: RunLoop.main)
            .sink { [weak self] _ in
                self?.changeSubject.send()
            }
            .store(in: &observers)
    }

    public func observeScribbles(for date: Date) -> AsyncStream<[Scribble]> {
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

    public func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> {
        let (stream, continuation) = AsyncStream.makeStream(of: [Scribble].self)

        let cancellable = changeSubject
            .prepend(())
            .sink { [weak self] _ in
                guard let self = self else { return }
                Task { @MainActor in
                    do {
                        let predicate = #Predicate<ScribbleEntity> { scribble in
                            scribble.createdAt >= startDate && scribble.createdAt < endDate
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

    public func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]> {
        let (stream, continuation) = AsyncStream.makeStream(of: [Scribble].self)

        let cancellable = changeSubject
            .prepend(())
            .sink { [weak self] _ in
                guard let self = self else { return }
                Task { @MainActor in
                    do {
                        // SwiftData predicates are limited with nested collections, 
                        // so we fetch all completed scribbles and filter in-memory for now.
                        // In a production app with many scribbles, we'd use a more efficient query.
                        let completedStatus = ScribbleStatus.completed.rawValue
                        let predicate = #Predicate<ScribbleEntity> { scribble in
                            scribble.status == completedStatus
                        }

                        let descriptor = FetchDescriptor<ScribbleEntity>(
                            predicate: predicate,
                            sortBy: [SortDescriptor(\.createdAt, order: .reverse)]
                        )

                        let entities = try self.modelContext.fetch(descriptor)
                        let scribbles = entities.map { $0.toDomain() }
                            .filter { scribble in
                                scribble.exercises.contains(where: { $0.canonicalName.lowercased() == exerciseName.lowercased() })
                            }

                        continuation.yield(scribbles)
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
            parsedJson: scribble.parsedJson
        )
        modelContext.insert(entity)
        let exercisesWithDate = scribble.exercises.map { $0.copy(createdAt: scribble.createdAt) }
        entity.exercises = try modelContext.syncExercises(for: exercisesWithDate)
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

            // Sync exercises
            let exercisesWithDate = scribble.exercises.map { $0.copy(createdAt: scribble.createdAt) }
            entity.exercises = try modelContext.syncExercises(for: exercisesWithDate)

            try modelContext.save()
            changeSubject.send()
        }
    }

    public func deleteScribble(id: UUID) async throws {
        let predicate = #Predicate<ScribbleEntity> { $0.id == id }
        var descriptor = FetchDescriptor<ScribbleEntity>(predicate: predicate)
        descriptor.fetchLimit = 1
        if let entity = try modelContext.fetch(descriptor).first {
            modelContext.delete(entity)
            try modelContext.save()
            changeSubject.send()
        }
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
            for exercise in scribble.exercises {
                modelContext.delete(exercise)
            }
            scribble.exercises = []
            try modelContext.save()
            changeSubject.send()
        }
    }

    public func confirmScribble(_ scribble: Scribble) async throws {
        let scribbleId = scribble.id
        let scribblePredicate = #Predicate<ScribbleEntity> { $0.id == scribbleId }
        var scribbleDescriptor = FetchDescriptor<ScribbleEntity>(predicate: scribblePredicate)
        scribbleDescriptor.fetchLimit = 1

        guard let scribbleEntity = try modelContext.fetch(scribbleDescriptor).first else {
            throw ScribbleError.notFound(scribbleId)
        }

        // Mark Scribble as Completed
        scribbleEntity.status = ScribbleStatus.completed.rawValue

        // Sync and Link Exercises
        let exercisesWithDate = scribble.exercises.map { $0.copy(createdAt: scribble.createdAt) }
        scribbleEntity.exercises = try modelContext.syncExercises(for: exercisesWithDate)

        try modelContext.save()
        changeSubject.send()
    }
}
