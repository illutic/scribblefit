import Foundation

public protocol AnalysisRepository: Sendable {
    // Read cached insights
    func getHomeSuggestion() async throws -> AnalysisSuggestion?
    func getSummary(period: SummaryPeriod) async throws -> AnalysisSummary?
    func getExerciseInsight(exerciseId: String) async throws -> ExerciseInsight?

    // Cache management
    func saveHomeSuggestion(_ suggestion: AnalysisSuggestion) async throws
    func saveSummary(_ summary: AnalysisSummary) async throws
    func saveExerciseInsight(_ insight: ExerciseInsight) async throws
    
    func clearOldInsights() async throws
}
