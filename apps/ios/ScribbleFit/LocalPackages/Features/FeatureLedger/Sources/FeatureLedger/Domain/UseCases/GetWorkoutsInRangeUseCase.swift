import Foundation
import CoreModel

@MainActor
public final class GetWorkoutsInRangeUseCase {
    private let repository: WorkoutRepository

    public init(repository: WorkoutRepository) {
        self.repository = repository
    }

    public func execute(startDate: Date, endDate: Date) -> AsyncStream<[Workout]> {
        repository.getWorkoutsInRange(startDate: startDate, endDate: endDate)
    }
}
