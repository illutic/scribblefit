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

    public func execute(date: Date, lookbackDays: Int = 7) async throws -> [AIInsight] {
        let calendar = Calendar.current
        let startDate = calendar.date(byAdding: .day, value: -lookbackDays, to: date) ?? date
        let workouts = try await workoutRepository.getWorkoutsInRange(startDate: startDate, endDate: date)
        let exercises = workouts.flatMap { $0.exercises }

        guard !exercises.isEmpty else {
            return [AIInsight(insightType: .summary, text: "Start your session by scribbling your first workout!")]
        }

        return try await llmProvider.generateInsightsSummary(exercises: exercises)
    }
}
