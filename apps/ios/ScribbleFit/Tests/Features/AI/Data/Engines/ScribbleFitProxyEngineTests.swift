import XCTest
@testable import ScribbleFit

final class ScribbleFitProxyEngineTests: XCTestCase {
    var engine: ScribbleFitProxyEngine!
    var session: URLSession!
    var networkClient: ScribbleFitNetworkClient!
    
    override func setUp() {
        super.setUp()
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [MockURLProtocol.self]
        session = URLSession(configuration: configuration)
        networkClient = ScribbleFitNetworkClient(session: session)
        engine = ScribbleFitProxyEngine(client: networkClient, systemPrompt: "test-prompt")
    }
    
    func testParseWorkoutSuccess() async throws {
        let jsonString = """
        {
            "date": "2024-03-03",
            "location": "Gym",
            "exercises": []
        }
        """
        
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, jsonString.data(using: .utf8)!)
        }
        
        let workout = try await engine.parseWorkout(rawText: "Bench 135x5")
        XCTAssertEqual(workout.date, "2024-03-03")
        XCTAssertEqual(workout.location, "Gym")
    }
}
