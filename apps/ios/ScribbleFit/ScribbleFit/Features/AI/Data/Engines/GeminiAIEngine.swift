import Foundation

/**
 * iOS implementation of GeminiAIEngine aligned with Android's.
 */
public actor GeminiAIEngine: LLMEngine, AnalysisEngine {
    private let session: URLSession
    private let secureKeyStorage: SecureKeyStorage
    private let configRepository: ConfigRepository
    private let jsonDecoder = JSONDecoder()
    private let jsonEncoder = JSONEncoder()
    
    private let apiBase = "https://generativelanguage.googleapis.com/v1beta"
    private var activeModelPath: String?
    
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
            guard let apiKey = try await secureKeyStorage.getApiKey(), !apiKey.isEmpty else {
                 throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "API key is missing"])
            }

            let config = await configRepository.getConfig()
            let systemPrompt = await (config?.promptText.isEmpty == false) ? config!.promptText : ScribbleFitProxyEngine.defaultPrompt
            
            let content = try await callGemini(apiKey: apiKey, prompt: systemPrompt, userMessage: "\(rawText)\n\nOutput in JSON format.")
            let duration = Int64(Date().timeIntervalSince(startTime) * 1000)
            
            let contentData = content.data(using: .utf8)!
            let dto = try jsonDecoder.decode(AIWorkoutDTO.self, from: contentData)
            
            return await ParsedWorkoutResult(
                workout: dto.toDomain(),
                rawText: rawText,
                status: .success,
                modelUsed: activeModelPath ?? "gemini-flash",
                processingTimeMs: duration
            )
        } catch {
            let duration = Int64(Date().timeIntervalSince(startTime) * 1000)
            return await ParsedWorkoutResult(
                workout: nil,
                rawText: rawText,
                status: .failure,
                modelUsed: activeModelPath ?? "gemini-flash",
                processingTimeMs: duration,
                error: error.localizedDescription
            )
        }
    }
    
    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        guard let apiKey = try await secureKeyStorage.getApiKey(), !apiKey.isEmpty else {
            throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "API key is missing"])
        }
        let content = try await callGemini(apiKey: apiKey, prompt: AnalysisPrompts.getSuggestionPrompt(context: context), userMessage: "Generate suggestion in JSON format.")
        let contentData = content.data(using: .utf8)!
        
        struct SuggestionDTO: Codable {
            let text: String
            let emoji: String
            let type: String
        }
        
        let dto = try jsonDecoder.decode(SuggestionDTO.self, from: contentData)
        return await AnalysisSuggestion(
            text: dto.text,
            emoji: dto.emoji,
            type: SuggestionType(rawValue: dto.type.lowercased()) ?? .pattern,
            timestamp: Date()
        )
    }
    
    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        guard let apiKey = try await secureKeyStorage.getApiKey(), !apiKey.isEmpty else {
            throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "API key is missing"])
        }
        let content = try await callGemini(apiKey: apiKey, prompt: AnalysisPrompts.getSummaryPrompt(period: period.rawValue, data: workoutData), userMessage: "Generate summary in JSON format.")
        let contentData = content.data(using: .utf8)!
        
        struct SummaryDTO: Codable {
            let summary_text: String
            let highlights: [String]
            let muscle_distribution: [MuscleStatDTO]
            let focus_area: String
            let volume_delta: Double
            
            enum CodingKeys: String, CodingKey {
                case summary_text, highlights, muscle_distribution, focus_area, volume_delta
            }
        }
        
        struct MuscleStatDTO: Codable {
            let muscle_group: String
            let volume_percentage: Double
            
            enum CodingKeys: String, CodingKey {
                case muscle_group, volume_percentage
            }
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
        guard let apiKey = try await secureKeyStorage.getApiKey(), !apiKey.isEmpty else {
            throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "API key is missing"])
        }
        let content = try await callGemini(apiKey: apiKey, prompt: AnalysisPrompts.getExerciseInsightPrompt(name: exerciseName, history: historyData), userMessage: "Analyze \(exerciseName) in JSON format.")
        let contentData = content.data(using: .utf8)!
        
        struct InsightDTO: Codable {
            let estimated_1rm: Double
            let pr_detected: Bool
            let trend_direction: String
            let breakdown_text: String
            
            enum CodingKeys: String, CodingKey {
                case estimated_1rm, pr_detected, trend_direction, breakdown_text
            }
        }
        
        let dto = try jsonDecoder.decode(InsightDTO.self, from: contentData)
        return await ExerciseInsight(
            exerciseId: exerciseName,
            estimated1RM: dto.estimated_1rm,
            prDetected: dto.pr_detected,
            trendDirection: InsightTrend(rawValue: dto.trend_direction.lowercased()) ?? .stable,
            breakdownText: dto.breakdown_text,
            timestamp: Date()
        )
    }
    
    private func getOrDiscoverModel(apiKey: String) async throws -> String {
        if let path = activeModelPath {
            return path
        }
        
        let url = URL(string: "\(apiBase)/models?key=\(apiKey)")!
        let (data, _) = try await session.data(from: url)
        let response = try JSONDecoder().decode(GeminiModelListResponse.self, from: data)
        
        let model = response.models
            .filter { $0.supportedGenerationMethods.contains("generateContent") }
            .filter { $0.name.localizedCaseInsensitiveContains("flash") }
            .sorted { $0.name > $1.name }
            .first
        
        let path = model?.name ?? "models/gemini-1.5-flash"
        
        activeModelPath = path
        
        return path
    }
    
    private func callGemini(apiKey: String, prompt: String, userMessage: String) async throws -> String {
        let modelPath = try await getOrDiscoverModel(apiKey: apiKey)
        let url = URL(string: "\(apiBase)/\(modelPath):generateContent?key=\(apiKey)")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let fullUserMessage = "\(prompt)\n\n\(userMessage)"
        
        let geminiRequest = GeminiRequest(
            contents: [GeminiContent(role: "user", parts: [GeminiPart(text: fullUserMessage)])],
            generationConfig: GeminiGenerationConfig(responseMimeType: "application/json")
        )
        
        request.httpBody = try jsonEncoder.encode(geminiRequest)
        
        let (data, response) = try await session.data(for: request)
        
        if let httpResponse = response as? HTTPURLResponse, !(200...299).contains(httpResponse.statusCode) {
            let errorBody = String(data: data, encoding: .utf8) ?? "Unknown error"
            throw NSError(domain: "GeminiAIEngine", code: httpResponse.statusCode, userInfo: [NSLocalizedDescriptionKey: "Server error: \(errorBody)"])
        }
        
        let geminiResponse = try JSONDecoder().decode(GeminiResponse.self, from: data)
        guard let content = geminiResponse.candidates.first?.content.parts.first?.text else {
            throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Empty response from Gemini (\(activeModelPath ?? modelPath))"])
        }
        
        return content
    }
}

// MARK: - Gemini DTOs

private struct GeminiModelListResponse: Codable {
    let models: [GeminiModelInfo]
}

private struct GeminiModelInfo: Codable {
    let name: String
    let supportedGenerationMethods: [String]
}

private struct GeminiRequest: Codable {
    let contents: [GeminiContent]
    let generationConfig: GeminiGenerationConfig
}

private struct GeminiContent: Codable {
    let role: String
    let parts: [GeminiPart]
}

private struct GeminiPart: Codable {
    let text: String
}

private struct GeminiGenerationConfig: Codable {
    let responseMimeType: String
    
    enum CodingKeys: String, CodingKey {
        case responseMimeType = "responseMimeType"
    }
}

private struct GeminiResponse: Codable {
    let candidates: [GeminiCandidate]
}

private struct GeminiCandidate: Codable {
    let content: GeminiContent
}
