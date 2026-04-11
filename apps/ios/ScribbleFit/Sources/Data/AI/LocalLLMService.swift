import Foundation

#if canImport(FoundationModels)
import FoundationModels
#endif

/**
 * LocalLLMService provides a version-agnostic interface for on-device LLM capabilities.
 * It internally handles iOS version checks to provide the best possible implementation.
 */
@MainActor
public final class LocalLLMService: LLMService {
    private let implementation: LLMService

    public init(configRepository: ConfigRepository) {
        if #available(iOS 26.0, *) {
            #if canImport(FoundationModels)
            self.implementation = NativeLocalLLMService(configRepository: configRepository)
            #else
            self.implementation = UnsupportedLocalLLMService()
            #endif
        } else {
            self.implementation = UnsupportedLocalLLMService()
        }
    }

    public func isSupported() async -> Bool {
        if #available(iOS 26.0, *) {
            return await (implementation as? NativeLocalLLMService)?.isSupported() ?? false
        } else {
            return false
        }
    }

    public func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        try await implementation.parseWorkout(rawText: rawText)
    }

    public func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        try await implementation.generateInsightsSummary(exercises: exercises)
    }

    public func validateApiKey(_ apiKey: String) async throws {
        try await implementation.validateApiKey(apiKey)
    }

    public func getAvailableModels(apiKey: String) async throws -> [String] {
        try await implementation.getAvailableModels(apiKey: apiKey)
    }
}

// MARK: - Native iOS 26+ Implementation

@available(iOS 26.0, *)
private final class NativeLocalLLMService: LLMService {
    private let configRepository: ConfigRepository
    
    #if canImport(FoundationModels)
    private let model = SystemLanguageModel.default
    #endif

    init(configRepository: ConfigRepository) {
        self.configRepository = configRepository
    }

    private var config: SystemConfig {
        configRepository.getConfig()
    }

    func isSupported() async -> Bool {
        #if canImport(FoundationModels)
        if case .available = model.availability {
            return true
        }
        #endif
        return false
    }

    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        #if canImport(FoundationModels)
        guard case .available = model.availability else {
            throw NSError(domain: "LocalLLMService", code: 503, userInfo: [NSLocalizedDescriptionKey: "Local model is not available: \(model.availability)"])
        }

        let session = LanguageModelSession(model: model)
        let prompt = "\(config.parsePrompt)\n\nInput: \(rawText)"
        
        let response = try await session.respond(
            to: prompt,
            generating: WorkoutResponse.self
        )
        
        let dto = response.content
        return ParsedWorkoutResult(
            exercises: dto.exercises.map { $0.toDomain() },
            rawText: rawText
        )
        #else
        throw NSError(domain: "LocalLLMService", code: 501, userInfo: [NSLocalizedDescriptionKey: "FoundationModels framework not found"])
        #endif
    }

    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        #if canImport(FoundationModels)
        guard case .available = model.availability else {
            throw NSError(domain: "LocalLLMService", code: 503, userInfo: [NSLocalizedDescriptionKey: "Local model is not available: \(model.availability)"])
        }

        let session = LanguageModelSession(model: model)
        let context = exercises.map { "\($0)" }.joined(separator: "\n")
        let prompt = "\(config.summaryPrompt)\n\nData:\n\(context)"

        let response = try await session.respond(
            to: prompt,
            generating: InsightsResponse.self
        )
        
        return response.content.insights.map { $0.toDomain() }
        #else
        throw NSError(domain: "LocalLLMService", code: 501, userInfo: [NSLocalizedDescriptionKey: "FoundationModels framework not found"])
        #endif
    }

    func validateApiKey(_ apiKey: String) async throws {
        return
    }

    func getAvailableModels(apiKey: String) async throws -> [String] {
        return ["foundation-model-default"]
    }
}

// MARK: - Unsupported Implementation (Fallback)

private final class UnsupportedLocalLLMService: LLMService {
    func isSupported() async -> Bool {
        return false
    }

    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        throw NSError(domain: "LocalLLMService", code: 501, userInfo: [NSLocalizedDescriptionKey: "Local LLM is not supported on this iOS version (requires iOS 26+)."])
    }

    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        throw NSError(domain: "LocalLLMService", code: 501, userInfo: [NSLocalizedDescriptionKey: "Local LLM is not supported on this iOS version (requires iOS 26+)."])
    }

    func validateApiKey(_ apiKey: String) async throws {
        return
    }

    func getAvailableModels(apiKey: String) async throws -> [String] {
        return []
    }
}

// MARK: - Guided Generation DTOs (iOS 26+)

#if canImport(FoundationModels)
@available(iOS 26.0, *)
@Generable
private struct WorkoutResponse {
    let exercises: [ExerciseDto]
}

@available(iOS 26.0, *)
@Generable
@MainActor
private struct InsightsResponse {
    let insights: [AIInsightResponse]
}

@available(iOS 26.0, *)
@Generable
@MainActor
private struct ExerciseDto {
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

@available(iOS 26.0, *)
@Generable
@MainActor
private struct SetDto {
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

@available(iOS 26.0, *)
@Generable
@MainActor
private struct AIInsightResponse {
    let insightType: String // "summary", "trend", "advice"
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
#endif
