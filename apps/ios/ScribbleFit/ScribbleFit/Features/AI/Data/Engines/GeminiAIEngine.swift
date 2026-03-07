import Foundation

public final class GeminiAIEngine: LLMEngine, AnalysisEngine {
    private let client: URLSession
    private let apiKey: String
    private let systemPrompt: String
    private let jsonDecoder = JSONDecoder()
    
    private let baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:"
    
    public init(apiKey: String, systemPrompt: String, client: URLSession = .shared) {
        self.apiKey = apiKey
        self.systemPrompt = systemPrompt
        self.client = client
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        let content = try await callGemini(prompt: systemPrompt, userMessage: rawText)
        let contentData = content.data(using: .utf8)!
        let dto = try jsonDecoder.decode(AIWorkoutDTO.self, from: contentData)
        return dto.toDomain()
    }
    
    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        let content = try await callGemini(prompt: AnalysisPrompts.getSuggestionPrompt(context: context), userMessage: "Generate suggestion.")
        let contentData = content.data(using: .utf8)!
        return try jsonDecoder.decode(AnalysisSuggestion.self, from: contentData)
    }
    
    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        let content = try await callGemini(prompt: AnalysisPrompts.getSummaryPrompt(period: period.rawValue, data: workoutData), userMessage: "Generate summary.")
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
        let content = try await callGemini(prompt: AnalysisPrompts.getExerciseInsightPrompt(name: exerciseName, history: historyData), userMessage: "Analyze \(exerciseName).")
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
    
    private func callGemini(prompt: String, userMessage: String) async throws -> String {
        let url = URL(string: "\(baseUrl)generateContent?key=\(apiKey)")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let geminiRequest = GeminiRequest(
            contents: [GeminiContent(parts: [GeminiPart(text: userMessage)])],
            systemInstruction: GeminiSystemInstruction(parts: [GeminiPart(text: prompt)]),
            generationConfig: GeminiGenerationConfig(responseMimeType: "application/json")
        )
        
        request.httpBody = try JSONEncoder().encode(geminiRequest)
        
        let (data, response) = try await client.data(for: request)
        
        if let httpResponse = response as? HTTPURLResponse, !(200...299).contains(httpResponse.statusCode) {
            throw NSError(domain: "GeminiAIEngine", code: httpResponse.statusCode, userInfo: [NSLocalizedDescriptionKey: "Server error"])
        }
        
        let geminiResponse = try JSONDecoder().decode(GeminiResponse.self, from: data)
        guard let content = geminiResponse.candidates.first?.content.parts.first?.text else {
            throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "No data"])
        }
        
        return content
    }
}

// MARK: - Gemini DTOs (Internal)

private struct GeminiRequest: Codable {
    let contents: [GeminiContent]
    let systemInstruction: GeminiSystemInstruction
    let generationConfig: GeminiGenerationConfig
}

private struct GeminiContent: Codable {
    let parts: [GeminiPart]
}

private struct GeminiSystemInstruction: Codable {
    let parts: [GeminiPart]
}

private struct GeminiPart: Codable {
    let text: String
}

private struct GeminiGenerationConfig: Codable {
    let responseMimeType: String
}

private struct GeminiResponse: Codable {
    let candidates: [GeminiCandidate]
}

private struct GeminiCandidate: Codable {
    let content: GeminiContent
}
