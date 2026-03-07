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
        
        // Define a simple DTO locally for summary response mapping if needed, 
        // but here AnalysisSummary is already Codable.
        // Note: Field names in JSON must match exactly or use CodingKeys.
        // To be safe, we'd use a DTO if the LLM output names differ (like summary_text).
        
        struct SummaryDTO: Codable {
            let summary_text: String
            let highlights: [String]
            let focus_muscle_groups: [String]
            let volume_delta: Double
        }
        
        let dto = try jsonDecoder.decode(SummaryDTO.self, from: contentData)
        return AnalysisSummary(
            period: period,
            summaryText: dto.summary_text,
            highlights: dto.highlights,
            focusMuscleGroups: dto.focus_muscle_groups,
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
            exerciseId: exerciseName, // Caller should normalize this
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
            throw NetworkError.serverError(httpResponse.statusCode)
        }
        
        let openAIResponse = try JSONDecoder().decode(OpenAIResponse.self, from: data)
        
        guard let content = openAIResponse.choices.first?.message.content else {
            throw NetworkError.noData
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
