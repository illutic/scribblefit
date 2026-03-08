import Combine
import Foundation

@MainActor
public final class AnalysisRepositoryImpl: AnalysisRepository {
    private let database: ScribbleFitDatabase
    nonisolated(unsafe) private let homeSuggestionSubject = CurrentValueSubject<AnalysisSuggestion?, Never>(nil)
    private let decoder = JSONDecoder()
    private let encoder = JSONEncoder()

    public init(database: ScribbleFitDatabase) {
        self.database = database
        if let cached = database.getInsightByKey(key: "home_suggestion"),
           let data = cached.jsonData.data(using: .utf8),
           let suggestion = try? decoder.decode(AnalysisSuggestion.self, from: data) {
            homeSuggestionSubject.send(suggestion)
        }
    }

    public nonisolated func getHomeSuggestion() -> AnyPublisher<AnalysisSuggestion?, Never> {
        homeSuggestionSubject.eraseToAnyPublisher()
    }

    public func getSummary(period: SummaryPeriod) async throws -> AnalysisSummary? {
        guard let cache = database.getInsightByKey(key: "summary_\(period.rawValue)"),
              let data = cache.jsonData.data(using: .utf8) else { return nil }
        return try? decoder.decode(AnalysisSummary.self, from: data)
    }

    public func getExerciseInsight(exerciseId: String) async throws -> ExerciseInsight? {
        guard let cache = database.getInsightByKey(key: "exercise_insight_\(exerciseId)"),
              let data = cache.jsonData.data(using: .utf8) else { return nil }
        return try? decoder.decode(ExerciseInsight.self, from: data)
    }

    public func saveHomeSuggestion(_ suggestion: AnalysisSuggestion) async throws {
        let data = try encoder.encode(suggestion)
        guard let json = String(data: data, encoding: .utf8) else { return }
        database.upsertInsight(InsightsCache(key: "home_suggestion", jsonData: json))
        homeSuggestionSubject.send(suggestion)
    }

    public func saveSummary(_ summary: AnalysisSummary) async throws {
        let data = try encoder.encode(summary)
        guard let json = String(data: data, encoding: .utf8) else { return }
        database.upsertInsight(InsightsCache(key: "summary_\(summary.period.rawValue)", jsonData: json))
    }

    public func saveExerciseInsight(_ insight: ExerciseInsight) async throws {
        let data = try encoder.encode(insight)
        guard let json = String(data: data, encoding: .utf8) else { return }
        database.upsertInsight(InsightsCache(key: "exercise_insight_\(insight.exerciseId)", jsonData: json))
    }

    public func clearOldInsights() async throws {
        database.clearInsights()
        homeSuggestionSubject.send(nil)
    }
}
