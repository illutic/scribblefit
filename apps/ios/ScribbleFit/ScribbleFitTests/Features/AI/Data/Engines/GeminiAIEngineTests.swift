import XCTest
@testable import ScribbleFit

final class GeminiAIEngineTests: XCTestCase {
    var engine: GeminiAIEngine!
    var session: URLSession!
    
    override func setUp() {
        super.setUp()
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [MockURLProtocol.self]
        session = URLSession(configuration: configuration)
        engine = GeminiAIEngine(apiKey: "test-key", systemPrompt: "test-prompt", session: session)
    }
    
    func testParseWorkoutSuccess() async throws {
        let nestedJson = #"{"date":"2024-03-03","exercises":[]}"#
        
        let responseObj: [String: Any] = [
            "candidates": [
                [
                    "content": [
                        "parts": [
                            ["text": nestedJson]
                        ]
                    ]
                ]
            ]
        ]
        
        let responseData = try JSONSerialization.data(withJSONObject: responseObj)
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, responseData)
        }
        
        let workout = try await engine.parseWorkout(rawText: "Bench 135x5")
        XCTAssertEqual(workout.date, "2024-03-03")
    }
}
