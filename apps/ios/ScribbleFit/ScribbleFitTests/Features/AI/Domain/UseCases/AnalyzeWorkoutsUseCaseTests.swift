import XCTest
@testable import ScribbleFit

final class AnalyzeWorkoutsUseCaseTests: XCTestCase {
    var repository: MockAnalysisRepository!
    var engine: MockAnalysisEngine!
    var useCase: AnalyzeWorkoutsUseCase!
    
    override func setUp() {
        super.setUp()
        repository = MockAnalysisRepository()
        engine = MockAnalysisEngine()
        useCase = AnalyzeWorkoutsUseCase(repository: repository, engine: engine)
    }
    
    func testRefreshHomeSuggestionSavesToRepository() async throws {
        let context = "Recent history"
        let suggestion = AnalysisSuggestion(text: "Push", emoji: "💪", type: .pattern, timestamp: Date())
        engine.suggestionResult = .success(suggestion)
        
        try await useCase.refreshHomeSuggestion(context: context)
        
        XCTAssertEqual(engine.capturedContext, context)
        XCTAssertEqual(repository.savedSuggestion?.text, "Push")
    }
}

// MARK: - Mocks

class MockAnalysisRepository: AnalysisRepository {
    var savedSuggestion: AnalysisSuggestion?
    
    func getHomeSuggestion() async throws -> AnalysisSuggestion? { nil }
    func getSummary(period: SummaryPeriod) async throws -> AnalysisSummary? { nil }
    func getExerciseInsight(exerciseId: String) async throws -> ExerciseInsight? { nil }
    
    func saveHomeSuggestion(_ suggestion: AnalysisSuggestion) async throws {
        savedSuggestion = suggestion
    }
    
    func saveSummary(_ summary: AnalysisSummary) async throws {}
    func saveExerciseInsight(_ insight: ExerciseInsight) async throws {}
    func clearOldInsights() async throws {}
}

class MockAnalysisEngine: AnalysisEngine {
    var suggestionResult: Result<AnalysisSuggestion, Error>!
    var capturedContext: String?
    
    func generateSuggestion(context: String) async throws -> AnalysisSuggestion {
        capturedContext = context
        return try suggestionResult.get()
    }
    
    func generateSummary(period: SummaryPeriod, workoutData: String) async throws -> AnalysisSummary {
        fatalError("Not implemented")
    }
    
    func generateExerciseInsight(exerciseName: String, historyData: String) async throws -> ExerciseInsight {
        fatalError("Not implemented")
    }
}
