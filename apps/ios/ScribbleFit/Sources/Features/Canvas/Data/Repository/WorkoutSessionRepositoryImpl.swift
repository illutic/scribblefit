import Foundation

@MainActor
public final class WorkoutSessionRepositoryImpl: WorkoutSessionRepository {
    private let database: ScribbleFitDatabase
    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()

    public init(database: ScribbleFitDatabase) {
        self.database = database
    }

    public func getActiveSession() async throws -> WorkoutSession? {
        guard let active = database.getActiveSession(),
              let data = active.jsonData.data(using: .utf8) else { return nil }
        return try? decoder.decode(WorkoutSession.self, from: data)
    }

    public func updateSession(_ session: WorkoutSession) async throws {
        let data = try encoder.encode(session)
        guard let json = String(data: data, encoding: .utf8) else { return }
        database.upsertActiveSession(ActiveSession(jsonData: json, updatedAt: Date()))
    }

    public func clearActiveSession() async throws {
        database.clearActiveSession()
    }
}
