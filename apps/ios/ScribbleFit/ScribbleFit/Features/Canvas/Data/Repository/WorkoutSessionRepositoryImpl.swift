import Foundation
import SwiftData

/**
 * iOS implementation of WorkoutSessionRepository using SwiftData.
 */
public final class WorkoutSessionRepositoryImpl: WorkoutSessionRepository {
    private let database: ScribbleFitDatabase
    private let jsonDecoder = JSONDecoder()
    private let jsonEncoder = JSONEncoder()

    public init(database: ScribbleFitDatabase) {
        self.database = database
    }

    @MainActor
    public convenience init() {
        self.init(database: .shared)
    }

    public func getActiveSession() async throws -> WorkoutSession? {
        guard let jsonData = await database.getActiveSession()?.jsonData.data(using: .utf8) else {
            return nil
        }
        return try jsonDecoder.decode(WorkoutSession.self, from: jsonData)
    }

    public func updateSession(_ session: WorkoutSession) async throws {
        let data = try jsonEncoder.encode(session)
        if let jsonString = String(data: data, encoding: .utf8) {
            let entity = ActiveSession(jsonData: jsonString, updatedAt: Date())
            await database.upsertActiveSession(entity)
        }
    }

    public func clearActiveSession() async throws {
        await database.clearActiveSession()
    }
}
