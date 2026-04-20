import Foundation
import CoreFirebase
import CoreModel

@MainActor
public final class GeminiLLMService: LLMService {
    private let configRepository: ConfigRepository
    
    private func getModel() -> GenerativeModel {
        // Firebase AI Logic handles authentication via GoogleService-Info.plist
        return FirebaseAI.firebaseAI(backend: .googleAI()).generativeModel(
            modelName: "gemini-2.5-flash-lite",
            generationConfig: GenerationConfig(responseMIMEType: "application/json")
        )
    }

    public init(configRepository: ConfigRepository, settingsRepository: SettingsRepository) {
        self.configRepository = configRepository
    }

    private var config: SystemConfig {
        configRepository.getConfig()
    }

    public func isSupported() async -> Bool {
        return true
    }

    public func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        let prompt = config.parsePrompt.replacingOccurrences(of: "{{rawText}}", with: rawText)
        
        let response = try await getModel().generateContent(prompt)
        guard let responseText = response.text else {
            throw NSError(domain: "GeminiLLMService", code: 500, userInfo: [NSLocalizedDescriptionKey: "No response from Gemini"])
        }
        
        let cleanJson = responseText.replacingOccurrences(of: "```json", with: "")
            .replacingOccurrences(of: "```", with: "")
            .trimmingCharacters(in: .whitespacesAndNewlines)
            
        guard let data = cleanJson.data(using: .utf8) else {
            throw NSError(domain: "GeminiLLMService", code: 400, userInfo: [NSLocalizedDescriptionKey: "Failed to convert response to data"])
        }
        
        let workoutResponse = try JSONDecoder().decode(WorkoutResponseDto.self, from: data)
        return ParsedWorkoutResult(
            exercises: workoutResponse.exercises.map { $0.toDomain() },
            rawText: rawText
        )
    }

    public func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        let context = exercises.map { "\($0)" }.joined(separator: "\n")
        let prompt = config.summaryPrompt.replacingOccurrences(of: "{{workoutData}}", with: context)

        let response = try await getModel().generateContent(prompt)
        guard let responseText = response.text else {
            throw NSError(domain: "GeminiLLMService", code: 500, userInfo: [NSLocalizedDescriptionKey: "No response from Gemini"])
        }
        
        let cleanJson = responseText.replacingOccurrences(of: "```json", with: "")
            .replacingOccurrences(of: "```", with: "")
            .trimmingCharacters(in: .whitespacesAndNewlines)
            
        guard let data = cleanJson.data(using: .utf8) else {
            throw NSError(domain: "GeminiLLMService", code: 400, userInfo: [NSLocalizedDescriptionKey: "Failed to convert response to data"])
        }
        
        let insightsResponse = try JSONDecoder().decode(InsightsResponseDto.self, from: data)
        return insightsResponse.insights.map { $0.toDomain() }
    }
}

// MARK: - DTOs

private struct WorkoutResponseDto: Codable {
    let exercises: [ExerciseDto]
}

private struct InsightsResponseDto: Codable {
    let insights: [AIInsightDto]
}

private struct ExerciseDto: Codable {
    let canonicalName: String
    let muscleGroup: String
    let sets: [SetDto]
    let estimated1rm: Float?
    let intensity: Float?

    func toDomain() -> Exercise {
        Exercise(
            id: UUID(),
            canonicalName: canonicalName,
            muscleGroup: muscleGroup,
            sets: sets.map { $0.toDomain() },
            isDraft: false,
            estimated1RM: estimated1rm,
            intensity: intensity
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

private struct AIInsightDto: Codable {
    let insightType: String
    let text: String
    
    func toDomain() -> AIInsight {
        let type: InsightType = {
            switch insightType.lowercased() {
            case "summary": return .summary
            case "trend": return .trend
            case "advice": return .advice
            default: return .summary
            }
        }()
        return AIInsight(insightType: type, text: text)
    }
}
