import Foundation
import CoreModel

@MainActor
public final class GetWorkoutsByDateUseCase {
    private let repository: WorkoutRepository

    public init(repository: WorkoutRepository) {
        self.repository = repository
    }

    public func execute(date: Date) -> AsyncStream<[Workout]> {
        return repository.getWorkouts(for: date)
    }
}
