import Foundation
import CoreModel
import CoreFirebase

/**
 * iOS implementation of GeminiAIEngine aligned with Android's.
 */
public actor GeminiAIEngine: LLMService {
    private let apiKey: String
    private let configRepository: ConfigRepository
    private let jsonDecoder = JSONDecoder()
    private let jsonEncoder = JSONEncoder()
    
    private let apiBase = "https://generativelanguage.googleapis.com/v1beta"
    private var activeModelPath: String?
    
    public init(
        apiKey: String,
        configRepository: ConfigRepository
    ) {
        self.apiKey = apiKey
        self.configRepository = configRepository
    }
    
    public func isSupported() async -> Bool {
        return !apiKey.isEmpty
    }

    public func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        guard !apiKey.isEmpty else {
             throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "API key is missing"])
        }

        let config = await configRepository.getConfig()
        let systemPrompt = (config?.promptText.isEmpty == false) ? config!.promptText : SystemConfig.defaultPrompt
        
        let content = try await callGemini(apiKey: apiKey, prompt: systemPrompt, userMessage: "\(rawText)\n\nOutput in JSON format.")
        
        let contentData = content.data(using: .utf8)!
        
        struct AIWorkoutDTO: Codable {
            let exercises: [ExerciseDTO]
            func toDomain() -> [Exercise] {
                return exercises.map { Exercise(id: UUID(), canonicalName: $0.canonicalName, muscleGroup: $0.muscleGroup, sets: $0.sets.map { ExerciseSet(id: UUID(), setNumber: $0.setNumber, weight: $0.weight, reps: $0.reps, rpe: $0.rpe, notes: $0.notes) }, isDraft: false) }
            }
        }
        
        struct ExerciseDTO: Codable {
            let canonicalName: String
            let muscleGroup: String
            let sets: [SetDTO]
        }
        
        struct SetDTO: Codable {
            let weight: Float?
            let reps: Int
            let setNumber: Int
            let rpe: Float?
            let notes: String?
        }

        let dto = try jsonDecoder.decode(AIWorkoutDTO.self, from: contentData)
        
        return ParsedWorkoutResult(
            exercises: dto.toDomain(),
            rawText: rawText
        )
    }
    
    public func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        guard !apiKey.isEmpty else {
            throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "API key is missing"])
        }
        let workoutData = exercises.map { "\($0)" }.joined(separator: "\n")
        let content = try await callGemini(apiKey: apiKey, prompt: "Generate summary of these workouts: \(workoutData)", userMessage: "Provide insights in JSON format.")
        let contentData = content.data(using: .utf8)!
        
        struct AIInsightDto: Codable {
            let insightType: String
            let text: String
        }
        
        struct InsightsResponseDto: Codable {
            let insights: [AIInsightDto]
        }
        
        let response = try jsonDecoder.decode(InsightsResponseDto.self, from: contentData)
        return response.insights.map { dto in
            let type: InsightType = {
                switch dto.insightType.lowercased() {
                case "summary": return .summary
                case "trend": return .trend
                case "advice": return .advice
                default: return .summary
                }
            }()
            return AIInsight(insightType: type, text: dto.text)
        }
    }
    
    public func generateExerciseInsight(history: String) async throws -> AIInsight {
        guard !apiKey.isEmpty else {
            throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "API key is missing"])
        }
        let content = try await callGemini(apiKey: apiKey, prompt: "Analyze this exercise history: \(history)", userMessage: "Provide insights in JSON format.")
        let contentData = content.data(using: .utf8)!
        
        struct AIInsightDto: Codable {
            let insightType: String
            let text: String
        }
        
        let dto = try jsonDecoder.decode(AIInsightDto.self, from: contentData)
        let type: InsightType = {
            switch dto.insightType.lowercased() {
            case "summary": return .summary
            case "trend": return .trend
            case "advice": return .advice
            default: return .summary
            }
        }()
        return AIInsight(insightType: type, text: dto.text)
    }
    
    private func getOrDiscoverModel(apiKey: String) async throws -> String {
        // If user has explicitly selected a model, use it
        let config = await configRepository.getConfig()
        if let selected = config?.preferredModel, !selected.isEmpty {
            activeModelPath = selected
            return selected
        }

        if let path = activeModelPath {
            return path
        }

        let url = URL(string: "\(apiBase)/models?key=\(apiKey)")!
        let (data, _) = try await URLSession.shared.data(from: url)
        
        struct GeminiModelListResponse: Codable {
            struct GeminiModelInfo: Codable {
                let name: String
                let supportedGenerationMethods: [String]
            }
            let models: [GeminiModelInfo]
        }
        
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
        
        struct GeminiRequest: Codable {
            struct GeminiContent: Codable {
                struct GeminiPart: Codable {
                    let text: String
                }
                let role: String
                let parts: [GeminiPart]
            }
            struct GeminiGenerationConfig: Codable {
                let responseMimeType: String
            }
            let contents: [GeminiContent]
            let generationConfig: GeminiGenerationConfig
        }
        
        let geminiRequest = GeminiRequest(
            contents: [GeminiRequest.GeminiContent(role: "user", parts: [GeminiRequest.GeminiContent.GeminiPart(text: fullUserMessage)])],
            generationConfig: GeminiRequest.GeminiGenerationConfig(responseMimeType: "application/json")
        )
        
        request.httpBody = try jsonEncoder.encode(geminiRequest)
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        if let httpResponse = response as? HTTPURLResponse, !(200...299).contains(httpResponse.statusCode) {
            let errorBody = String(data: data, encoding: .utf8) ?? "Unknown error"
            throw NSError(domain: "GeminiAIEngine", code: httpResponse.statusCode, userInfo: [NSLocalizedDescriptionKey: "Server error: \(errorBody)"])
        }
        
        struct GeminiResponse: Codable {
            struct GeminiCandidate: Codable {
                struct GeminiContent: Codable {
                    struct GeminiPart: Codable {
                        let text: String
                    }
                    let parts: [GeminiPart]
                }
                let content: GeminiContent
            }
            let candidates: [GeminiCandidate]
        }
        
        let geminiResponse = try JSONDecoder().decode(GeminiResponse.self, from: data)
        guard let content = geminiResponse.candidates.first?.content.parts.first?.text else {
            throw NSError(domain: "GeminiAIEngine", code: 0, userInfo: [NSLocalizedDescriptionKey: "Empty response from Gemini"])
        }
        
        return content
    }
}
