import Foundation

public protocol AnalysisEngine: Sendable {
    func generateSuggestion(context: String) async throws -> AnalysisSuggestion
    func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary
    func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight
}
