import Foundation
import CoreModel

#if canImport(FoundationModels)
import FoundationModels
#endif

/**
 * LocalLLMService provides a version-agnostic interface for on-device LLM capabilities.
 * It uses a bridge pattern to safely handle futuristic iOS 26+ APIs while maintaining
 * compatibility with iOS 17+.
 */
@MainActor
public final class LocalLLMService: LLMService {
    private let implementation: LLMService

    public init(configRepository: ConfigRepository) {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, macOS 26.0, *) {
            self.implementation = NativeLocalLLMService(configRepository: configRepository)
        } else {
            self.implementation = UnsupportedLocalLLMService()
        }
        #else
        self.implementation = UnsupportedLocalLLMService()
        #endif
    }

    public func isSupported() async -> Bool {
        await implementation.isSupported()
    }

    public func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        try await implementation.parseWorkout(rawText: rawText)
    }

    public func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        try await implementation.generateInsightsSummary(exercises: exercises)
    }
}

// MARK: - Native iOS 26+ Implementation

#if canImport(FoundationModels)
@available(iOS 26.0, macOS 26.0, *)
@MainActor
private final class NativeLocalLLMService: LLMService {
    private let configRepository: ConfigRepository
    private let model = SystemLanguageModel.default

    init(configRepository: ConfigRepository) {
        self.configRepository = configRepository
    }

    func isSupported() async -> Bool {
        return true
    }

    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        let config = configRepository.getConfig()
        let prompt = config.parsePrompt.replacingOccurrences(of: "{{rawText}}", with: rawText)
        let session = LanguageModelSession(model: model)
        let response = try await session.respond(
            to: prompt,
            generating: WorkoutResponse.self
        )
        return ParsedWorkoutResult(
            exercises: response.content.exercises.map { $0.toDomain() },
            rawText: rawText
        )
    }

    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        let config = configRepository.getConfig()
        let context = exercises.map { "\($0)" }.joined(separator: "\n")
        let prompt = config.summaryPrompt.replacingOccurrences(of: "{{workoutData}}", with: context)
        let session = LanguageModelSession(model: model)
        let response = try await session.respond(
            to: prompt,
            generating: InsightsResponse.self
        )
        return response.content.insights.map { $0.toDomain() }
    }
}
#endif

// MARK: - Fallback Implementation

@MainActor
private final class UnsupportedLocalLLMService: LLMService {
    func isSupported() async -> Bool { false }
    
    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        throw NSError(domain: "LocalLLM", code: 501, userInfo: [NSLocalizedDescriptionKey: "Local AI not supported on this device/OS version"])
    }

    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] {
        throw NSError(domain: "LocalLLM", code: 501, userInfo: [NSLocalizedDescriptionKey: "Local AI not supported on this device/OS version"])
    }
}

// MARK: - Guided Generation DTOs (iOS 26+)

#if canImport(FoundationModels)
@available(iOS 26.0, macOS 26.0, *)
@Generable
private struct WorkoutResponse: Sendable {
    let exercises: [ExerciseDto]
}

@available(iOS 26.0, macOS 26.0, *)
@Generable
private struct InsightsResponse: Sendable {
    let insights: [AIInsightResponse]
}

@available(iOS 26.0, macOS 26.0, *)
@Generable
@MainActor
private struct ExerciseDto: Codable, Sendable {
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

@available(iOS 26.0, macOS 26.0, *)
@Generable
@MainActor
private struct SetDto: Codable, Sendable {
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

@available(iOS 26.0, macOS 26.0, *)
@Generable
@MainActor
private struct AIInsightResponse: Sendable {
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
