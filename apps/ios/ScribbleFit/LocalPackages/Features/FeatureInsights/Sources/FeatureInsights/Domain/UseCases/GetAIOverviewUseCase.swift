import Foundation
import CoreModel

@MainActor
public final class GetAIOverviewUseCase {
    private let scribbleRepository: ScribbleRepository
    private let llmProvider: LLMService

    public init(scribbleRepository: ScribbleRepository, llmProvider: LLMService) {
        self.scribbleRepository = scribbleRepository
        self.llmProvider = llmProvider
    }

    public func execute(date: Date, lookbackDays: Int = 7) async throws -> [AIInsight] {
        let calendar = Calendar.current
        let startDate = calendar.date(byAdding: .day, value: -lookbackDays, to: date) ?? date
        let stream = scribbleRepository.observeScribbles(startDate: startDate, endDate: date)
        
        var scribbles: [Scribble] = []
        for await value in stream {
            scribbles = value
            break
        }
        
        let completedScribbles = scribbles.filter { $0.status == .completed }
        let exercises = completedScribbles.flatMap { $0.exercises }

        guard !exercises.isEmpty else {
            return [AIInsight(insightType: .summary, text: "Start your session by scribbling your first workout!")]
        }

        return try await llmProvider.generateInsightsSummary(exercises: exercises)
    }
}
