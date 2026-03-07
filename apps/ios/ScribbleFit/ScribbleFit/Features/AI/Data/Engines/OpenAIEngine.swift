import Foundation

public final class OpenAIEngine: LLMEngine, AnalysisEngine {
    private let secureKeyStorage: SecureKeyStorage
    private let configRepository: ConfigRepository
    private let session: URLSession
    private let jsonDecoder = JSONDecoder()
    
    public init(
        secureKeyStorage: SecureKeyStorage,
        configRepository: ConfigRepository,
        session: URLSession = .shared
    ) {
        self.secureKeyStorage = secureKeyStorage
        self.configRepository = configRepository
        self.session = session
    }
    
    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        let startTime = Date()
        do {
            let apiKey = try await secureKeyStorage.getApiKey() ?? ""
            let config = await configRepository.getConfig()
            let instructions = config?.promptText ?? ScribbleFitProxyEngine.defaultPrompt
            
            let response = try await callOpenAIResponse(apiKey: apiKey, instructions: instructions, userMessage: rawText)
            let duration = Int64(Date().timeIntervalSince(startTime) * 1000)
            
            guard let content = response.output.first(where: { $0.type == "message" })?.content?.first(where: { $0.type == "text" })?.text else {
                throw NSError(domain: "OpenAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Empty response"])
            }
            
            let reasoning = response.output.first(where: { $0.type == "reasoning" })?.content?.first(where: { $0.type == "text" })?.text
            
            let contentData = content.data(using: .utf8)!
            let serializableWorkout = try jsonDecoder.decode(AIWorkoutDTO.self, from: contentData)
            
            return ParsedWorkoutResult(
                workout: serializableWorkout.toDomain(),
                rawText = rawText,
                status: .success,
                modelUsed: "gpt-4o-mini",
                processingTimeMs: duration,
                reasoning: reasoning
            )
        } catch {
            let duration = Int64(Date().timeIntervalSince(startTime) * 1000)
            return ParsedWorkoutResult(
                workout: nil,
                rawText: rawText,
                status: .failure,
                modelUsed: "gpt-4o-mini",
                processingTimeMs: duration,
                error: error.localizedDescription
            )
        }
    }
    
    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        let apiKey = try await secureKeyStorage.getApiKey() ?? ""
        let prompt = AnalysisPrompts.getSuggestionPrompt(context: context)
        let response = try await callOpenAIResponse(apiKey: apiKey, instructions: prompt, userMessage: "Generate suggestion in JSON format.")
        
        guard let content = response.output.first(where: { $0.type == "message" })?.content?.first(where: { $0.type == "text" })?.text else {
            throw NSError(domain: "OpenAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Empty response"])
        }
        
        return try jsonDecoder.decode(AnalysisSuggestion.self, from: content.data(using: .utf8)!)
    }
    
    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        let apiKey = try await secureKeyStorage.getApiKey() ?? ""
        let prompt = AnalysisPrompts.getSummaryPrompt(period: period.rawValue, data: workoutData)
        let response = try await callOpenAIResponse(apiKey: apiKey, instructions: prompt, userMessage: "Generate summary in JSON format.")
        
        guard let content = response.output.first(where: { $0.type == "message" })?.content?.first(where: { $0.type == "text" })?.text else {
            throw NSError(domain: "OpenAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Empty response"])
        }
        
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
        
        let dto = try jsonDecoder.decode(SummaryDTO.self, from: content.data(using: .utf8)!)
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
    
    public func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight {
        let apiKey = try await secureKeyStorage.getApiKey() ?? ""
        let prompt = AnalysisPrompts.getExerciseInsightPrompt(name: exerciseName, history: historyData)
        let response = try await callOpenAIResponse(apiKey: apiKey, instructions: prompt, userMessage: "Analyze \(exerciseName) in JSON format.")
        
        guard let content = response.output.first(where: { $0.type == "message" })?.content?.first(where: { $0.type == "text" })?.text else {
            throw NSError(domain: "OpenAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Empty response"])
        }
        
        struct InsightDTO: Codable {
            let estimated_1rm: Double
            let pr_detected: Bool
            let trend_direction: String
            let breakdown_text: String
        }
        
        let dto = try jsonDecoder.decode(InsightDTO.self, from: content.data(using: .utf8)!)
        return ExerciseInsight(
            exerciseId: exerciseName,
            estimated1RM: dto.estimated_1rm,
            prDetected: dto.pr_detected,
            trendDirection: InsightTrend(rawValue: dto.trend_direction.lowercased()) ?? .stable,
            breakdownText: dto.breakdown_text,
            timestamp: Date()
        )
    }
    
    private func callOpenAIResponse(apiKey: String, instructions: String, userMessage: String) async throws -> OpenAIResponse {
        let url = URL(string: "https://api.openai.com/v1/responses")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let openAIRequest = OpenAIResponseRequest(
            model: "gpt-4o-mini",
            input: .string("\(userMessage)\n\nOutput in JSON format."),
            instructions: instructions,
            text: .init(format: .init(type: "json_object"))
        )
        
        request.httpBody = try JSONEncoder().encode(openAIRequest)
        
        let (data, response) = try await session.data(for: request)
        
        if let httpResponse = response as? HTTPURLResponse, !(200...299).contains(httpResponse.statusCode) {
            let errorBody = String(data: data, encoding: .utf8) ?? "Unknown error"
            throw NSError(domain: "OpenAIEngine", code: httpResponse.statusCode, userInfo: [NSLocalizedDescriptionKey: "Server error: \(errorBody)"])
        }
        
        return try jsonDecoder.decode(OpenAIResponse.self, from: data)
    }
}

// MARK: - OpenAI Responses API DTOs

private struct OpenAIResponseRequest: Codable {
    let model: String
    let input: OpenAIInput
    let instructions: String
    let text: OpenAITextConfig
}

private enum OpenAIInput: Codable {
    case string(String)
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        switch self {
        case .string(let s): try container.encode(s)
        }
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        self = .string(try container.decode(String.self))
    }
}

private struct OpenAITextConfig: Codable {
    let format: OpenAIFormatConfig
}

private struct OpenAIFormatConfig: Codable {
    let type: String
}

private struct OpenAIResponse: Codable {
    let id: String?
    let output: [OpenAIOutputItem]
}

private struct OpenAIOutputItem: Codable {
    let id: String?
    let type: String?
    let content: [OpenAIContentPart]?
}

private struct OpenAIContentPart: Codable {
    let type: String
    let text: String
}
