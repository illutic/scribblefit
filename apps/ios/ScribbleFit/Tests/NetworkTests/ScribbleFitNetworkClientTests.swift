import XCTest
@testable import ScribbleFit

final class ScribbleFitNetworkClientTests: XCTestCase {
    var client: ScribbleFitNetworkClient!
    
    override func setUp() {
        super.setUp()
        let configuration = URLSessionConfiguration.ephemeral
        configuration.protocolClasses = [MockURLProtocol.self]
        let session = URLSession(configuration: configuration)
        client = ScribbleFitNetworkClient(session: session)
    }
    
    func testGetMetadata() async throws {
        let jsonString = """
        {
            "status": "ok",
            "version": "1.2.3"
        }
        """
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, jsonString.data(using: .utf8)!)
        }
        
        let metadata = try await client.getMetadata()
        XCTAssertEqual(metadata.status, "ok")
        XCTAssertEqual(metadata.version, "1.2.3")
    }
    
    func testGetPromptConfig() async throws {
        let jsonString = """
        {
            "version": "1.0.0",
            "prompt": "Test Prompt"
        }
        """
        MockURLProtocol.requestHandler = { request in
            let response = HTTPURLResponse(url: request.url!, statusCode: 200, httpVersion: nil, headerFields: nil)!
            return (response, jsonString.data(using: .utf8)!)
        }
        
        let config = try await client.getPromptConfig()
        XCTAssertEqual(config.version, "1.0.0")
        XCTAssertEqual(config.prompt, "Test Prompt")
    }
}

class MockURLProtocol: URLProtocol {
    nonisolated(unsafe) static var requestHandler: ((URLRequest) throws -> (HTTPURLResponse, Data))?
    
    override class func canInit(with request: URLRequest) -> Bool {
        return true
    }
    
    override class func canonicalRequest(for request: URLRequest) -> URLRequest {
        return request
    }
    
    override func startLoading() {
        guard let handler = MockURLProtocol.requestHandler else {
            XCTFail("Handler is nil.")
            return
        }
        
        do {
            let (response, data) = try handler(request)
            client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
            client?.urlProtocol(self, didLoad: data)
            client?.urlProtocolDidFinishLoading(self)
        } catch {
            client?.urlProtocol(self, didFailWithError: error)
        }
    }
    
    override func stopLoading() {}
}
