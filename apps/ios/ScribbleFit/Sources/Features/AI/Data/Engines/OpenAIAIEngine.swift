import Foundation

private let openAIBaseURL = "https://api.openai.com/v1"
private let defaultOpenAIModel = "gpt-4o-mini"

private let suggestionPrompt = """
You are ScribbleFit AI, a fitness analysis assistant.
Generate one actionable training suggestion based on the workout context below.
Output ONLY this JSON (no markdown, no extra text):
{"text":"suggestion text","emoji":"emoji","type":"recovery|pattern|milestone|rest"}
type must be exactly one of: recovery, pattern, milestone, rest
"""

private let summaryPrompt = """
You are ScribbleFit AI, a fitness analysis assistant.
Analyze the workout data below and generate a training summary.
Output ONLY this JSON (no markdown, no extra text):
{"summaryText":"2-3 sentence summary","highlights":["highlight 1","highlight 2"],"muscleDistribution":[{"muscleGroup":"name","volumePercentage":number}],"focusArea":"primary muscle group","volumeDelta":number}
muscleDistribution percentages must sum to 100. volumeDelta is percentage change vs previous period.
"""

private let insightPrompt = """
You are ScribbleFit AI, a fitness analysis assistant.
Analyze the exercise history below and generate a performance insight.
Output ONLY this JSON (no markdown, no extra text):
{"estimated1RM":number,"prDetected":true|false,"trendDirection":"improving|stable|plateaued|declining","breakdownText":"2-3 sentence analysis"}
Use Epley formula (weight * (1 + reps/30)) for 1RM estimate. trendDirection must be exactly one of: improving, stable, plateaued, declining
"""

public final class OpenAIAIEngine: LLMEngine, AnalysisEngine {
    private let networkClient: ScribbleFitNetworkClient
    private let secureKeyStorage: any SecureKeyStorage
    private let prompt: String
    private let preferredModel: String

    public init(
        networkClient: ScribbleFitNetworkClient,
        secureKeyStorage: any SecureKeyStorage,
        prompt: String,
        preferredModel: String = ""
    ) {
        self.networkClient = networkClient
        self.secureKeyStorage = secureKeyStorage
        self.prompt = prompt
        self.preferredModel = preferredModel
    }

    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        let start = Date()
        guard let apiKey = await secureKeyStorage.getApiKey() else {
            return ParsedWorkoutResult(workout: nil, rawText: rawText, status: .failure, error: "No API key")
        }
        let model = preferredModel.isEmpty ? defaultOpenAIModel : preferredModel
        do {
            let responseText = try await callOpenAI(apiKey: apiKey, model: model, userPrompt: "\(prompt)\n\nInput: \(rawText)")
            guard let data = responseText.data(using: .utf8),
                  let workout = try? JSONDecoder().decode(ParsedWorkout.self, from: data) else {
                return ParsedWorkoutResult(workout: nil, rawText: rawText, status: .failure, error: "Parse failed")
            }
            let ms = Int64(Date().timeIntervalSince(start) * 1000)
            return ParsedWorkoutResult(workout: workout, rawText: rawText, status: .success, modelUsed: model, processingTimeMs: ms)
        } catch {
            return ParsedWorkoutResult(workout: nil, rawText: rawText, status: .failure, error: error.localizedDescription)
        }
    }

    public func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        guard let apiKey = await secureKeyStorage.getApiKey() else {
            throw NSError(domain: "OpenAIAIEngine", code: -1, userInfo: [NSLocalizedDescriptionKey: "No API key"])
        }
        let model = preferredModel.isEmpty ? "gpt-4o-mini" : preferredModel
        let responseText = try await callOpenAI(apiKey: apiKey, model: model, userPrompt: "\(suggestionPrompt)\n\nContext:\n\(context)")
        guard let data = responseText.data(using: .utf8) else { throw URLError(.cannotParseResponse) }
        let dto = try JSONDecoder().decode(SuggestionResponse.self, from: data)
        return AnalysisSuggestion(text: dto.text, emoji: dto.emoji, type: dto.type)
    }

    public func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        guard let apiKey = await secureKeyStorage.getApiKey() else {
            throw NSError(domain: "OpenAIAIEngine", code: -1, userInfo: [NSLocalizedDescriptionKey: "No API key"])
        }
        let model = preferredModel.isEmpty ? "gpt-4o-mini" : preferredModel
        let responseText = try await callOpenAI(apiKey: apiKey, model: model, userPrompt: "\(summaryPrompt)\n\nPeriod: \(period.rawValue)\nData:\n\(workoutData)")
        guard let data = responseText.data(using: .utf8) else { throw URLError(.cannotParseResponse) }
        let dto = try JSONDecoder().decode(SummaryResponse.self, from: data)
        return AnalysisSummary(period: period, summaryText: dto.summaryText, highlights: dto.highlights, muscleDistribution: dto.muscleDistribution, focusArea: dto.focusArea, volumeDelta: dto.volumeDelta)
    }

    public func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight {
        guard let apiKey = await secureKeyStorage.getApiKey() else {
            throw NSError(domain: "OpenAIAIEngine", code: -1, userInfo: [NSLocalizedDescriptionKey: "No API key"])
        }
        let model = preferredModel.isEmpty ? "gpt-4o-mini" : preferredModel
        let responseText = try await callOpenAI(apiKey: apiKey, model: model, userPrompt: "\(insightPrompt)\n\nExercise: \(exerciseName)\nHistory:\n\(historyData)")
        guard let data = responseText.data(using: .utf8) else { throw URLError(.cannotParseResponse) }
        let dto = try JSONDecoder().decode(InsightResponse.self, from: data)
        return ExerciseInsight(exerciseId: exerciseName, estimated1RM: dto.estimated1RM, prDetected: dto.prDetected, trendDirection: dto.trendDirection, breakdownText: dto.breakdownText)
    }

    private func callOpenAI(apiKey: String, model: String, userPrompt: String) async throws -> String {
        let request = OpenAIChatRequest(
            model: model,
            messages: [OpenAIChatMessage(role: "user", content: userPrompt)],
            responseFormat: OpenAIResponseFormat(type: "json_object")
        )
        guard let url = URL(string: "\(openAIBaseURL)/chat/completions") else { throw URLError(.badURL) }
        let response: OpenAIChatResponse = try await networkClient.post(
            url: url,
            body: request,
            headers: ["Authorization": "Bearer \(apiKey)"]
        )
        return response.choices.first?.message.content ?? ""
    }
}
