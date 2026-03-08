import Combine
import Foundation

public protocol AnalysisRepository: Sendable {
    func getHomeSuggestion() -> AnyPublisher<AnalysisSuggestion?, Never>
    func getSummary(period: SummaryPeriod) async throws -> AnalysisSummary?
    func getExerciseInsight(exerciseId: String) async throws -> ExerciseInsight?
    func saveHomeSuggestion(_ suggestion: AnalysisSuggestion) async throws
    func saveSummary(_ summary: AnalysisSummary) async throws
    func saveExerciseInsight(_ insight: ExerciseInsight) async throws
    func clearOldInsights() async throws
}
