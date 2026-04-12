import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

public struct GetWorkoutsInRangeUseCase: Sendable {
    private let repository: WorkoutRepository

    public init(repository: WorkoutRepository) {
        self.repository = repository
    }

    public func execute(startDate: Date, endDate: Date) async throws -> [Workout] {
        try await repository.getWorkoutsInRange(startDate: startDate, endDate: endDate)
    }
}
