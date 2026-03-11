import Foundation
#if canImport(FoundationModels)
import FoundationModels
#endif

public final class LocalAIEngine: LLMEngine {
    public init() {}

    public func parseWorkout(rawText: String) async -> ParsedWorkoutResult {
        if #available(iOS 26.0, macOS 26.0, *) {
            return await parseWithAppleIntelligence(rawText: rawText)
        }
        return ParsedWorkoutResult(
            workout: nil,
            rawText: rawText,
            status: .failure,
            error: "Local AI requires iOS 26 or later with Apple Intelligence enabled"
        )
    }

    @available(iOS 26.0, macOS 26.0, *)
    private func parseWithAppleIntelligence(rawText: String) async -> ParsedWorkoutResult {
        let start = Date()
        guard SystemLanguageModel.default.isAvailable else {
            return ParsedWorkoutResult(
                workout: nil,
                rawText: rawText,
                status: .failure,
                error: "Apple Intelligence is not available on this device"
            )
        }
        do {
            let session = LanguageModelSession(instructions: SystemConfigDomain.PARSE_PROMPT)
            let response = try await session.respond(to: rawText)
            let cleanString = response.content
                .replacingOccurrences(of: "```json", with: "")
                .replacingOccurrences(of: "```", with: "")
                .trimmingCharacters(in: .whitespacesAndNewlines)
            
            guard let data = cleanString.data(using: .utf8),
                  let workout = try? JSONDecoder().decode(ParsedWorkout.self, from: data) else {
                return ParsedWorkoutResult(
                    workout: nil, rawText: rawText, status: .failure, error: "Parse failed"
                )
            }
            let ms = Int64(Date().timeIntervalSince(start) * 1000)
            return ParsedWorkoutResult(
                workout: workout, rawText: rawText, status: .success,
                modelUsed: "apple-intelligence", processingTimeMs: ms
            )
        } catch {
            return ParsedWorkoutResult(
                workout: nil, rawText: rawText, status: .failure,
                error: error.localizedDescription
            )
        }
    }
}
