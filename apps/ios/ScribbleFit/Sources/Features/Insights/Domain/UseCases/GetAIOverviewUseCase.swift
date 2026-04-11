import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

@MainActor
public final class GetAIOverviewUseCase {
    private let workoutRepository: WorkoutRepository
    private let llmProvider: LLMService

    public init(workoutRepository: WorkoutRepository, llmProvider: LLMService) {
        self.workoutRepository = workoutRepository
        self.llmProvider = llmProvider
    }

    public func execute(date: Date) async throws -> [AIInsight] {
        // Fetch workouts for the date to provide context to the AI
        let workoutsSnapshot = try await fetchWorkoutsSnapshot(for: date)
        let exercises = workoutsSnapshot.flatMap { $0.exercises }
        
        guard !exercises.isEmpty else {
            return [AIInsight(insightType: .summary, text: "Start your session by scribbling your first workout!")]
        }
        
        return try await llmProvider.generateInsightsSummary(exercises: exercises)
    }
    
    private func fetchWorkoutsSnapshot(for date: Date) async throws -> [Workout] {
        var workouts: [Workout] = []
        let stream = workoutRepository.getWorkouts(for: date)
        for await snapshot in stream {
            workouts = snapshot
            break // Just take the first snapshot
        }
        return workouts
    }
}
