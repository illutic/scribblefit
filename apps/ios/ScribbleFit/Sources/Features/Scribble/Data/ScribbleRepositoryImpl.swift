import Foundation
import SwiftData
@preconcurrency import Combine
#if SWIFT_PACKAGE
import CoreModel
import CoreDatabase
#endif

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
        let entity = scribble.toEntity()
        modelContext.insert(entity)
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
            
            // Re-map exercises
            // Clear existing ones first to prevent duplication
            for exercise in entity.exercises {
                modelContext.delete(exercise)
            }
            entity.exercises = scribble.exercises.map { $0.toEntity() }
            
            try modelContext.save()
            changeSubject.send()
        }
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
            // Because ExerciseEntity -> SetEntity is cascade, 
            // and ScribbleEntity -> ExerciseEntity is cascade,
            // we can just clear the array and save if SwiftData is configured correctly,
            // OR manually delete each exercise to be safe.
            for exercise in scribble.exercises {
                modelContext.delete(exercise)
            }
            scribble.exercises = []
            try modelContext.save()
            changeSubject.send()
        }
    }
}
