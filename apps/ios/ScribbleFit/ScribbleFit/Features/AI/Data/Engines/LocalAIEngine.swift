import Foundation

#if canImport(FoundationModels)
import FoundationModels
#endif

/**
 * Local AI Engine for iOS.
 * Leverages Apple Intelligence via the FoundationModels framework.
 */
public final class LocalAIEngine: LLMEngine, AnalysisEngine {
    private let systemPrompt: String
    private let jsonDecoder = JSONDecoder()
    
    public init(systemPrompt: String) {
        self.systemPrompt = systemPrompt
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            let session = LanguageModelSession { self.systemPrompt }
            let response = try await session.respond(to: "Parse this gym note: \(rawText)", generating: LocalAIWorkoutDTO.self)
            return response.content.toDomain()
        }
        #endif
        throw AIParsingError(rawText: rawText, error: "Local AI Engine not supported")
    }
    
    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            let session = LanguageModelSession { AnalysisPrompts.getSuggestionPrompt(context: context) }
            let response = try await session.respond(to: "Generate suggestion.", generating: AnalysisSuggestion.self)
            return response.content
        }
        #endif
        throw AIParsingError(rawText: "Suggestion", error: "Local AI Engine not supported")
    }
    
    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            let session = LanguageModelSession { AnalysisPrompts.getSummaryPrompt(period: period.rawValue, data: workoutData) }
            
            struct SummaryDTO: Codable {
                let summary_text: String
                let highlights: [String]
                let focus_muscle_groups: [String]
                let volume_delta: Double
            }
            
            let response = try await session.respond(to: "Generate summary.", generating: SummaryDTO.self)
            let dto = response.content
            return AnalysisSummary(
                period: period,
                summaryText: dto.summary_text,
                highlights: dto.highlights,
                focusMuscleGroups: dto.focus_muscle_groups,
                volumeDelta: dto.volume_delta,
                timestamp: Date()
            )
        }
        #endif
        throw AIParsingError(rawText: "Summary", error: "Local AI Engine not supported")
    }
    
    public func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            let session = LanguageModelSession { AnalysisPrompts.getExerciseInsightPrompt(name: exerciseName, history: historyData) }
            
            struct InsightDTO: Codable {
                let estimated_1rm: Double
                let pr_detected: Bool
                let trend_direction: String
                let breakdown_text: String
            }
            
            let response = try await session.respond(to: "Analyze \(exerciseName).", generating: InsightDTO.self)
            let dto = response.content
            return ExerciseInsight(
                exerciseId: exerciseName,
                estimated1RM: dto.estimated_1rm,
                prDetected: dto.pr_detected,
                trendDirection: InsightTrend(rawValue: dto.trend_direction.lowercased()) ?? .stable,
                breakdownText: dto.breakdown_text,
                timestamp: Date()
            )
        }
        #endif
        throw AIParsingError(rawText: "Insight", error: "Local AI Engine not supported")
    }
    
    public func isAvailable() async -> Bool {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            return SystemLanguageModel.default.availability == .available
        }
        #endif
        return false
    }
}
