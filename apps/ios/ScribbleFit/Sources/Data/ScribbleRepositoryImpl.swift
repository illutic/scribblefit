import Foundation
import SwiftData
@preconcurrency import Combine

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
        AsyncStream { continuation in
            let calendar = Calendar.current
            let startOfDay = calendar.startOfDay(for: date)
            let endOfDay = calendar.date(byAdding: .day, value: 1, to: startOfDay)!
            
            let predicate = #Predicate<ScribbleEntity> { scribble in
                scribble.createdAt >= startOfDay && scribble.createdAt < endOfDay
            }
            
            let descriptor = FetchDescriptor<ScribbleEntity>(predicate: predicate, sortBy: [SortDescriptor(\.createdAt)])
            
            let cancellable = changeSubject
                .prepend(()) // Fetch immediately on start
                .sink { [weak self] _ in
                    guard let self = self else { return }
                    Task { @MainActor in
                        do {
                            let entities = try self.modelContext.fetch(descriptor)
                            continuation.yield(entities.map { $0.toDomain() })
                        } catch {
                            continuation.yield([])
                        }
                    }
                }
            
            continuation.onTermination = { _ in
                cancellable.cancel()
            }
        }
    }

    public func addScribble(_ scribble: Scribble) async throws {
        let entity = ScribbleEntity(
            id: scribble.id,
            rawText: scribble.rawText,
            parsedJson: scribble.parsedJson,
            status: scribble.status.rawValue,
            createdAt: scribble.createdAt
        )
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
            entity.parsedJson = scribble.parsedJson
            entity.status = scribble.status.rawValue
            entity.createdAt = scribble.createdAt
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
}

extension ScribbleEntity {
    func toDomain() -> Scribble {
        Scribble(
            id: id,
            rawText: rawText,
            parsedJson: parsedJson,
            status: ScribbleStatus(rawValue: status) ?? .raw,
            createdAt: createdAt,
            exercises: [] // SwiftData for exercises can be added later
        )
    }
}
