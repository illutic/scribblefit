import XCTest
@testable import ScribbleFit

final class LocalAIEngineTests: XCTestCase {
    var engine: LocalAIEngine!
    
    override func setUp() {
        super.setUp()
        engine = LocalAIEngine(systemPrompt: "test-prompt")
    }
    
    func testParseWorkoutFailureWhenUnavailable() async throws {
        let isAvailable = await engine.isAvailable()
        XCTAssertFalse(isAvailable)
        
        do {
            _ = try await engine.parseWorkout(rawText: "Bench 135x5")
            XCTFail("Expected error to be thrown")
        } catch {
            // Expected
        }
    }
}
