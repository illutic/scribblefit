import Foundation

public final class GeminiAIEngine: LLMEngine, AnalysisEngine {
    private let networkClient: ScribbleFitNetworkClient
    private let secureKeyStorage: any SecureKeyStorage
    private let prompt: String

    private static let baseURL = "https://generativelanguage.googleapis.com/v1beta"

    public init(networkClient: ScribbleFitNetworkClient, secureKeyStorage: any SecureKeyStorage, prompt: String) {
        self.networkClient = networkClient
        self.secureKeyStorage = secureKeyStorage
        self.prompt = prompt
    }

    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        let start = Date()
        guard let apiKey = await secureKeyStorage.getApiKey() else {
            return ParsedWorkoutResult(workout: nil, rawText: rawText, status: .failure, error: "No API key")
        }
        do {
            let responseText = try await callGemini(apiKey: apiKey, userPrompt: "\(prompt)\n\nInput: \(rawText)")
            guard let data = responseText.data(using: .utf8),
                  let workout = try? JSONDecoder().decode(ParsedWorkout.self, from: data) else {
                return ParsedWorkoutResult(workout: nil, rawText: rawText, status: .failure, error: "Parse failed")
            }
            let ms = Int64(Date().timeIntervalSince(start) * 1000)
            return ParsedWorkoutResult(workout: workout, rawText: rawText, status: .success, processingTimeMs: ms)
        } catch {
            return ParsedWorkoutResult(workout: nil, rawText: rawText, status: .failure, error: error.localizedDescription)
        }
    }

    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        throw NSError(domain: "GeminiAIEngine", code: -1, userInfo: [NSLocalizedDescriptionKey: "Not implemented"])
    }

    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        throw NSError(domain: "GeminiAIEngine", code: -1, userInfo: [NSLocalizedDescriptionKey: "Not implemented"])
    }

    public func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight {
        throw NSError(domain: "GeminiAIEngine", code: -1, userInfo: [NSLocalizedDescriptionKey: "Not implemented"])
    }

    private func callGemini(apiKey: String, userPrompt: String) async throws -> String {
        let request = GeminiRequest(
            contents: [GeminiContent(parts: [GeminiPart(text: userPrompt)])],
            generationConfig: GeminiGenerationConfig(responseMimeType: "application/json")
        )
        guard let url = URL(string: "\(Self.baseURL)/models/gemini-1.5-flash:generateContent?key=\(apiKey)") else {
            throw URLError(.badURL)
        }
        let response: GeminiResponse = try await networkClient.post(url: url, body: request)
        return response.candidates.first?.content.parts.first?.text ?? ""
    }
}
