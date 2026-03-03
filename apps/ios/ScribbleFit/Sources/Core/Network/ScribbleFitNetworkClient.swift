import Foundation

public enum NetworkError: Error {
    case invalidURL
    case noData
    case decodingError
    case serverError(Int)
}

public final class ScribbleFitNetworkClient: Sendable {
    public static let shared = ScribbleFitNetworkClient()
    
    private let baseURL = URL(string: "http://localhost:8080/")!
    private let session: URLSession
    
    public init(session: URLSession = .shared) {
        self.session = session
    }
    
    public func getMetadata() async throws -> MetadataResponse {
        let url = baseURL.appendingPathComponent("api/sync/metadata")
        return try await fetchData(from: url)
    }
    
    public func getPromptConfig() async throws -> ConfigResponse {
        let url = baseURL.appendingPathComponent("api/config/prompt")
        return try await fetchData(from: url)
    }
    
    private func fetchData<T: Codable>(from url: URL) async throws -> T {
        let (data, response) = try await session.data(from: url)
        
        if let httpResponse = response as? HTTPURLResponse, !(200...299).contains(httpResponse.statusCode) {
            throw NetworkError.serverError(httpResponse.statusCode)
        }
        
        do {
            let decoder = JSONDecoder()
            return try decoder.decode(T.self, from: data)
        } catch {
            throw NetworkError.decodingError
        }
    }
}
