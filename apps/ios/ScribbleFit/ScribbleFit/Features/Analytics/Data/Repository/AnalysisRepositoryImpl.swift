import Foundation
import SwiftData

/**
 * iOS implementation of AnalysisRepository using ScribbleFitDatabase (SwiftData).
 */
public final class AnalysisRepositoryImpl: AnalysisRepository {
    private let database: ScribbleFitDatabase
    private let jsonDecoder = JSONDecoder()
    private let jsonEncoder = JSONEncoder()
    
    public init(database: ScribbleFitDatabase = .shared) {
        self.database = database
    }
    
    public func getHomeSuggestion() async throws -> AnalysisSuggestion? {
        guard let jsonData = database.getInsightByKey(key: KEY_HOME_SUGGESTION)?.jsonData.data(using: .utf8) else {
            return nil
        }
        return try jsonDecoder.decode(AnalysisSuggestion.self, from: jsonData)
    }
    
    public func getSummary(period: SummaryPeriod) async throws -> AnalysisSummary? {
        let key = "\(KEY_SUMMARY_PREFIX)_\(period.rawValue)"
        guard let jsonData = database.getInsightByKey(key: key)?.jsonData.data(using: .utf8) else {
            return nil
        }
        return try jsonDecoder.decode(AnalysisSummary.self, from: jsonData)
    }
    
    public func getExerciseInsight(exerciseId: String) async throws -> ExerciseInsight? {
        let key = "\(KEY_EXERCISE_PREFIX)_\(exerciseId)"
        guard let jsonData = database.getInsightByKey(key: key)?.jsonData.data(using: .utf8) else {
            return nil
        }
        return try jsonDecoder.decode(ExerciseInsight.self, from: jsonData)
    }
    
    public func saveHomeSuggestion(_ suggestion: AnalysisSuggestion) async throws {
        let data = try jsonEncoder.encode(suggestion)
        if let jsonString = String(data: data, encoding: .utf8) {
            let entity = InsightsCache(
                key: KEY_HOME_SUGGESTION,
                jsonData: jsonString,
                createdAt: Date()
            )
            database.upsertInsight(entity)
        }
    }
    
    public func saveSummary(_ summary: AnalysisSummary) async throws {
        let key = "\(KEY_SUMMARY_PREFIX)_\(summary.period.rawValue)"
        let data = try jsonEncoder.encode(summary)
        if let jsonString = String(data: data, encoding: .utf8) {
            let entity = InsightsCache(
                key: key,
                jsonData: jsonString,
                createdAt: Date()
            )
            database.upsertInsight(entity)
        }
    }
    
    public func saveExerciseInsight(_ insight: ExerciseInsight) async throws {
        let key = "\(KEY_EXERCISE_PREFIX)_\(insight.exerciseId)"
        let data = try jsonEncoder.encode(insight)
        if let jsonString = String(data: data, encoding: .utf8) {
            let entity = InsightsCache(
                key: key,
                jsonData: jsonString,
                createdAt: Date()
            )
            database.upsertInsight(entity)
        }
    }
    
    public func clearOldInsights() async throws {
        database.clearInsights()
    }
    
    private let KEY_HOME_SUGGESTION = "home_suggestion"
    private let KEY_SUMMARY_PREFIX = "summary"
    private let KEY_EXERCISE_PREFIX = "exercise_insight"
}
