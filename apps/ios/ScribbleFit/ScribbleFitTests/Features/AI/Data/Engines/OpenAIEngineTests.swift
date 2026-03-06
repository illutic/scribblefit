import XCTest
@testable import ScribbleFit

final class OpenAIEngineTests: XCTestCase {
    var engine: OpenAIEngine!
    var session: URLSession!
    
    override func setUp() {
        super.setUp()
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [MockURLProtocol.self]
        session = URLSession(configuration: configuration)
        engine = OpenAIEngine(apiKey: "test-key", systemPrompt: "test-prompt", session: session)
    }
    
    func testParseWorkoutSuccess() async throws {
        // We need to return a JSON where 'content' is a string containing another JSON
        let nestedJson = #"{"date":"2024-03-03","exercises":[]}"#
        
        let responseObj: [String: Any] = [
            "choices": [
                [
                    "message": [
                        "role": "assistant",
                        "content": nestedJson
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
    
    func testParseWorkoutFailure() async throws {
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 500, httpVersion: nil, headerFields: nil)!
            return (response, Data())
        }
        
        do {
            _ = try await engine.parseWorkout(rawText: "Bench 135x5")
            XCTFail("Expected error to be thrown")
        } catch {
            // Success
        }
    }
}
