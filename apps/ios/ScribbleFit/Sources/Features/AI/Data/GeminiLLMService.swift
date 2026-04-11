import Foundation
import CoreModel

@MainActor
public final class GeminiLLMService: LLMService {
    private let configRepository: ConfigRepository
    private let settingsRepository: SettingsRepository
    private let baseURL = "https://generativelanguage.googleapis.com/v1beta"
    private let session: URLSession

    public init(configRepository: ConfigRepository, settingsRepository: SettingsRepository, session: URLSession = .shared) {
        self.configRepository = configRepository
        self.settingsRepository = settingsRepository
        self.session = session
    }

    private var config: SystemConfig {
        configRepository.getConfig()
    }

    private func getApiKey() async throws -> String {
        guard let apiKey = try await settingsRepository.getApiKey(), !apiKey.isEmpty else {
            throw NSError(domain: "GeminiLLMService", code: 401, userInfo: [NSLocalizedDescriptionKey: "API Key is not provided"])
        }
        return apiKey
    }

    public func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        let apiKey = try await getApiKey()
        let prompt = "\(config.parsePrompt)\n\nInput: \(rawText)"
        
        let responseText = try await callGemini(apiKey: apiKey, userPrompt: prompt)
        
        let jsonData = Data(responseText.utf8)
        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        
        let dto = try decoder.decode(WorkoutDto.self, from: jsonData)
        return ParsedWorkoutResult(exercises: dto.exercises.map { $0.toDomain() }, rawText: rawText)
    }

    public func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        let apiKey = try await getApiKey()
        
        let context = exercises.map { exercise in
            let sets = exercise.sets.map { "\($0.weight)x\($0.reps)" }.joined(separator: ", ")
            return "\(exercise.canonicalName) (\(exercise.muscleGroup)): \(sets)"
        }.joined(separator: "\n")
        
        let prompt = "\(config.summaryPrompt)\n\nData:\n\(context)"

        let responseText = try await callGemini(apiKey: apiKey, userPrompt: prompt)
        
        let jsonData = Data(responseText.utf8)
        let decoder = JSONDecoder()
        // We use camelCase in the prompt but snake_case might be returned depending on LLM behavior.
        // Let's assume the LLM follows the prompt exactly.
        return try decoder.decode([AIInsight].self, from: jsonData)
    }

    public func validateApiKey(_ apiKey: String) async throws {
        let url = URL(string: "\(baseURL)/models?key=\(apiKey)")!
        let (_, response) = try await session.data(from: url)
        
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            throw NSError(domain: "GeminiLLMService", code: 401, userInfo: [NSLocalizedDescriptionKey: "Invalid API Key"])
        }
    }

    public func getAvailableModels(apiKey: String) async throws -> [String] {
        let url = URL(string: "\(baseURL)/models?key=\(apiKey)")!
        let (data, response) = try await session.data(from: url)
        
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            let code = (response as? HTTPURLResponse)?.statusCode ?? 500
            throw NSError(domain: "GeminiLLMService", code: code, userInfo: [NSLocalizedDescriptionKey: "Failed to fetch models"])
        }
        
        let list = try JSONDecoder().decode(ModelListResponse.self, from: data)
        return list.models
            .filter { $0.supportedGenerationMethods.contains("generateContent") }
            .map { $0.name.replacingOccurrences(of: "models/", with: "") }
    }

    private func callGemini(apiKey: String, userPrompt: String) async throws -> String {
        let model = config.preferredModel ?? "gemini-1.5-flash"
        let url = URL(string: "\(baseURL)/models/\(model):generateContent?key=\(apiKey)")!
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let geminiRequest = GeminiRequest(
            contents: [Content(parts: [Part(text: userPrompt)])]
        )
        
        request.httpBody = try JSONEncoder().encode(geminiRequest)
        
        let (data, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            let errorText = String(data: data, encoding: .utf8) ?? "Unknown error"
            throw NSError(domain: "GeminiLLMService", code: (response as? HTTPURLResponse)?.statusCode ?? 500, userInfo: [NSLocalizedDescriptionKey: "Gemini API error: \(errorText)"])
        }
        
        let geminiResponse = try JSONDecoder().decode(GeminiResponse.self, from: data)
        let resultText = geminiResponse.candidates.first?.content.parts.first?.text ?? ""
        
        return resultText
            .replacingOccurrences(of: "```json", with: "")
            .replacingOccurrences(of: "```", with: "")
            .trimmingCharacters(in: .whitespacesAndNewlines)
    }
}

// MARK: - Internal DTOs

private struct WorkoutDto: Codable {
    let exercises: [ExerciseDto]
}

private struct ExerciseDto: Codable {
    let canonicalName: String
    let muscleGroup: String
    let sets: [SetDto]
    let estimated1rm: Float?
    let intensity: Float?
    let improvement: Float?
    
    func toDomain() -> Exercise {
        Exercise(
            id: UUID(),
            canonicalName: canonicalName,
            muscleGroup: muscleGroup,
            sets: sets.map { $0.toDomain() },
            isDraft: false,
            estimated1RM: estimated1rm,
            intensity: intensity,
            improvement: improvement
        )
    }
}

private struct SetDto: Codable {
    let weight: Float
    let reps: Int
    let setNumber: Int
    let rpe: Float?
    let notes: String?
    
    func toDomain() -> ExerciseSet {
        ExerciseSet(
            id: UUID(),
            setNumber: setNumber,
            weight: weight,
            reps: reps,
            rpe: rpe,
            notes: notes
        )
    }
}

private struct ModelListResponse: Codable {
    let models: [ModelDto]
}

private struct ModelDto: Codable {
    let name: String
    let supportedGenerationMethods: [String]
}

private struct GeminiRequest: Codable {
    let contents: [Content]
}

private struct Content: Codable {
    let parts: [Part]
}

private struct Part: Codable {
    let text: String
}

private struct GeminiResponse: Codable {
    let candidates: [Candidate]
}

private struct Candidate: Codable {
    let content: Content
}
