import Foundation

public final class UserRepositoryImpl: UserRepository {
    private let ledgerRepository: LedgerRepository

    public init(ledgerRepository: LedgerRepository) {
        self.ledgerRepository = ledgerRepository
    }

    public func getUserStats() async throws -> UserStats {
        let history = try await ledgerRepository.getWorkoutHistory()

        let totalVolume = history.reduce(0.0) { $0 + $1.totalVolume }
        let firstWorkoutDate = history.map { $0.date }.min() ?? Date()

        return UserStats(
            totalWorkouts: history.count,
            lifetimeVolume: totalVolume,
            prCount: 0, // Placeholder
            joinDate: firstWorkoutDate
        )
    }
}
