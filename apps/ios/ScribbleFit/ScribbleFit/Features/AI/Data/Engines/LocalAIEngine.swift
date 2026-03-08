import Foundation

#if canImport(FoundationModels)
import FoundationModels
#endif

/**
 * Local AI Engine for iOS.
 * Leverages Apple Intelligence via the FoundationModels framework.
 */
public final class LocalAIEngine: LLMEngine, AnalysisEngine {
    private let configRepository: ConfigRepository
    private let jsonDecoder = JSONDecoder()
    
    public init(configRepository: ConfigRepository) {
        self.configRepository = configRepository
    }
    
    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        let startTime = Date()
        do {
            #if canImport(FoundationModels)
            if #available(iOS 26.0, *) {
                let config = await configRepository.getConfig()
                let systemPrompt = config?.promptText ?? ScribbleFitProxyEngine.defaultPrompt
                 
                let session = LanguageModelSession { systemPrompt }
                let response = try await session.respond(to: "Parse this gym note: \(rawText)")
                let duration = Int64(Date().timeIntervalSince(startTime) * 1000)
                
                let cleanString = response.content
                    .replacingOccurrences(of: "```json", with: "")
                    .replacingOccurrences(of: "```", with: "")
                    .trimmingCharacters(in: .whitespacesAndNewlines)

                // 2. Convert the sanitized string to Data
                if let cleanData = cleanString.data(using: .utf8) {
                    // 3. Decode using the cleanData, not the original data
                    let dto = try jsonDecoder.decode(AIWorkoutDTO.self, from: cleanData)
                    
                    return ParsedWorkoutResult(
                        workout: dto.toDomain(),
                        rawText: rawText,
                        status: .success,
                        modelUsed: "apple-intelligence-local",
                        processingTimeMs: duration
                    )
                }
            }
            #endif
            throw NSError(domain: "LocalAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Local AI Engine not supported"])
        } catch {
            let duration = Int64(Date().timeIntervalSince(startTime) * 1000)
            return ParsedWorkoutResult(
                workout: nil,
                rawText: rawText,
                status: .failure,
                modelUsed: "apple-intelligence-local",
                processingTimeMs: duration,
                error: error.localizedDescription
            )
        }
    }
    
    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            let session = LanguageModelSession { AnalysisPrompts.getSuggestionPrompt(context: context) }
            let response = try await session.respond(to: "Generate suggestion.")
            if let data = response.content.data(using: .utf8) {
                struct SuggestionDTO: Codable {
                    let text: String
                    let emoji: String
                    let type: String
                }
                let dto = try jsonDecoder.decode(SuggestionDTO.self, from: data)
                return AnalysisSuggestion(
                    text: dto.text,
                    emoji: dto.emoji,
                    type: SuggestionType(rawValue: dto.type.lowercased()) ?? .pattern,
                    timestamp: Date()
                )
            }
        }
        #endif
        throw NSError(domain: "LocalAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Local AI Engine not supported"])
    }
    
    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            let session = LanguageModelSession { AnalysisPrompts.getSummaryPrompt(period: period.rawValue, data: workoutData) }
            let response = try await session.respond(to: "Generate summary.")
            if let data = response.content.data(using: .utf8) {
                struct SummaryDTO: Codable {
                    let summary_text: String
                    let highlights: [String]
                    let muscle_distribution: [MuscleStatDTO]
                    let focus_area: String
                    let volume_delta: Double
                }
                
                struct MuscleStatDTO: Codable {
                    let muscle_group: String
                    let volume_percentage: Double
                }
                
                let dto = try jsonDecoder.decode(SummaryDTO.self, from: data)
                return AnalysisSummary(
                    period: period,
                    summaryText: dto.summary_text,
                    highlights: dto.highlights,
                    muscleDistribution: dto.muscle_distribution.map { MuscleGroupStat(muscleGroup: $0.muscle_group, volumePercentage: $0.volume_percentage) },
                    focusArea: dto.focus_area,
                    volumeDelta: dto.volume_delta,
                    timestamp: Date()
                )
            }
        }
        #endif
        throw NSError(domain: "LocalAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Local AI Engine not supported"])
    }
    
    public func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            let session = LanguageModelSession { AnalysisPrompts.getExerciseInsightPrompt(name: exerciseName, history: historyData) }
            let response = try await session.respond(to: "Analyze \(exerciseName).")
            if let data = response.content.data(using: .utf8) {
                struct InsightDTO: Codable {
                    let estimated_1rm: Double
                    let pr_detected: Bool
                    let trend_direction: String
                    let breakdown_text: String
                }
                
                let dto = try jsonDecoder.decode(InsightDTO.self, from: data)
                return ExerciseInsight(
                    exerciseId: exerciseName,
                    estimated1RM: dto.estimated_1rm,
                    prDetected: dto.pr_detected,
                    trendDirection: InsightTrend(rawValue: dto.trend_direction.lowercased()) ?? .stable,
                    breakdownText: dto.breakdown_text,
                    timestamp: Date()
                )
            }
        }
        #endif
        throw NSError(domain: "LocalAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Local AI Engine not supported"])
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
