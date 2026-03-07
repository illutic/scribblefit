import Foundation

/**
 * UseCase to trigger a background analysis refresh.
 */
public final class AnalyzeWorkoutsUseCase {
    private let repository: AnalysisRepository
    private let engine: AnalysisEngine
    
    public init(repository: AnalysisRepository, engine: AnalysisEngine) {
        self.repository = repository
        self.engine = engine
    }
    
    public func refreshHomeSuggestion(context: String) async throws {
        let suggestion = try await engine.generateSuggestion(context: context)
        try await repository.saveHomeSuggestion(suggestion)
    }
    
    public func refreshSummary(period: SummaryPeriod, workoutData: String) async throws {
        let summary = try await engine.generateSummary(period: period, workoutData: workoutData)
        try await repository.saveSummary(summary)
    }
    
    public func refreshExerciseInsight(exerciseId: String, exerciseName: String, historyData: String) async throws {
        let insight = try await engine.generateExerciseInsight(exerciseName: exerciseName, historyData: historyData)
        // Ensure exerciseId matches local database ID
        let normalizedInsight = ExerciseInsight(
            exerciseId: exerciseId,
            estimated1RM: insight.estimated1RM,
            prDetected: insight.prDetected,
            trendDirection: insight.trendDirection,
            breakdownText: insight.breakdownText,
            timestamp: insight.timestamp
        )
        try await repository.saveExerciseInsight(normalizedInsight)
    }
}
