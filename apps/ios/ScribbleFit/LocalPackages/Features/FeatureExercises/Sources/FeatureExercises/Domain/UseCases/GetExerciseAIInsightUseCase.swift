import Foundation
import CoreModel

@MainActor
public final class GetExerciseAIInsightUseCase {
    private let llmService: LLMService
    
    public init(llmService: LLMService) {
        self.llmService = llmService
    }
    
    public func execute(history: [ExerciseHistorySession]) async throws -> AIInsight {
        guard !history.isEmpty else {
            throw NSError(domain: "ExerciseAIInsight", code: 404, userInfo: [NSLocalizedDescriptionKey: "No history available to generate insights"])
        }
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .medium
        
        let historyContext = history.prefix(5).map { session in
            let date = dateFormatter.string(from: session.date)
            let sets = session.exercise.sets.map { "\($0.weight ?? 0)x\($0.reps)" }.joined(separator: ", ")
            return "\(date): \(sets)"
        }.joined(separator: "\n")
        
        return try await llmService.generateExerciseInsight(history: historyContext)
    }
}
