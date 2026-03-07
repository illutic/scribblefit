import Foundation

public final class OpenAIEngine: LLMEngine, AnalysisEngine {
    private let apiKey: String
    private let systemPrompt: String
    private let session: URLSession
    private let jsonDecoder = JSONDecoder()
    
    public init(apiKey: String, systemPrompt: String, session: URLSession = .shared) {
        self.apiKey = apiKey
        self.systemPrompt = systemPrompt
        self.session = session
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        let content = try await callOpenAI(prompt: systemPrompt, userMessage: rawText)
        let contentData = content.data(using: .utf8)!
        let serializableWorkout = try jsonDecoder.decode(AIWorkoutDTO.self, from: contentData)
        return serializableWorkout.toDomain()
    }
    
    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        let prompt = AnalysisPrompts.getSuggestionPrompt(context: context)
        let content = try await callOpenAI(prompt: prompt, userMessage: "Generate suggestion.")
        let contentData = content.data(using: .utf8)!
        return try jsonDecoder.decode(AnalysisSuggestion.self, from: contentData)
    }
    
    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        let prompt = AnalysisPrompts.getSummaryPrompt(period: period.rawValue, data: workoutData)
        let content = try await callOpenAI(prompt: prompt, userMessage: "Generate summary.")
        let contentData = content.data(using: .utf8)!
        
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
        
        let dto = try jsonDecoder.decode(SummaryDTO.self, from: contentData)
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
        let prompt = AnalysisPrompts.getExerciseInsightPrompt(name: exerciseName, history: historyData)
        let content = try await callOpenAI(prompt: prompt, userMessage: "Analyze \(exerciseName).")
        let contentData = content.data(using: .utf8)!
        
        struct InsightDTO: Codable {
            let estimated_1rm: Double
            let pr_detected: Bool
            let trend_direction: String
            let breakdown_text: String
        }
        
        let dto = try jsonDecoder.decode(InsightDTO.self, from: contentData)
        return ExerciseInsight(
            exerciseId: exerciseName,
            estimated1RM: dto.estimated_1rm,
            prDetected: dto.pr_detected,
            trendDirection: InsightTrend(rawValue: dto.trend_direction.lowercased()) ?? .stable,
            breakdownText: dto.breakdown_text,
            timestamp: Date()
        )
    }
    
    private func callOpenAI(prompt: String, userMessage: String) async throws -> String {
        let url = URL(string: "https://api.openai.com/v1/chat/completions")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let openAIRequest = OpenAIRequest(
            model: "gpt-4o-mini",
            messages: [
                OpenAIMessage(role: "system", content: prompt),
                OpenAIMessage(role: "user", content: userMessage)
            ],
            responseFormat: OpenAIResponseFormat(type: "json_object")
        )
        
        request.httpBody = try JSONEncoder().encode(openAIRequest)
        
        let (data, response) = try await session.data(for: request)
        
        if let httpResponse = response as? HTTPURLResponse, !(200...299).contains(httpResponse.statusCode) {
            throw NSError(domain: "OpenAIEngine", code: httpResponse.statusCode, userInfo: [NSLocalizedDescriptionKey: "Server error"])
        }
        
        let openAIResponse = try JSONDecoder().decode(OpenAIResponse.self, from: data)
        
        guard let content = openAIResponse.choices.first?.message.content else {
            throw NSError(domain: "OpenAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "No data"])
        }
        
        return content
    }
}

// MARK: - OpenAI DTOs (Internal)

private struct OpenAIRequest: Codable {
    let model: String
    let messages: [OpenAIMessage]
    let responseFormat: OpenAIResponseFormat
    
    enum CodingKeys: String, CodingKey {
        case model, messages
        case responseFormat = "response_format"
    }
}

private struct OpenAIMessage: Codable {
    let role: String
    let content: String
}

private struct OpenAIResponseFormat: Codable {
    let type: String
}

private struct OpenAIResponse: Codable {
    let choices: [OpenAIChoice]
}

private struct OpenAIChoice: Codable {
    let message: OpenAIMessage
}
