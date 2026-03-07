import XCTest
@testable import ScribbleFit

final class UserRepositoryImplTests: XCTestCase {
    var ledgerRepository: MockLedgerRepository!
    var repository: UserRepositoryImpl!
    
    override func setUp() {
        super.setUp()
        ledgerRepository = MockLedgerRepository()
        repository = UserRepositoryImpl(ledgerRepository: ledgerRepository)
    }
    
    func testGetUserStatsAggregatesData() async throws {
        let history = [
            WorkoutHistory(id: "1", date: Date(timeIntervalSince1970: 1000), location: nil, totalVolume: 500.0, exercises: []),
            WorkoutHistory(id: "2", date: Date(timeIntervalSince1970: 2000), location: nil, totalVolume: 1000.0, exercises: [])
        ]
        ledgerRepository.history = history
        
        let stats = try await repository.getUserStats()
        
        XCTAssertEqual(stats.totalWorkouts, 2)
        XCTAssertEqual(stats.lifetimeVolume, 1500.0)
        XCTAssertEqual(stats.joinDate.timeIntervalSince1970, 1000)
    }
}

class MockLedgerRepository: LedgerRepository {
    var history: [WorkoutHistory] = []
    func getWorkoutHistory() async throws -> [WorkoutHistory] { history }
    func logWorkout(_ workout: WorkoutHistory) async throws {}
}
