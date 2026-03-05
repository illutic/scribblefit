import Foundation

public final class OpenAIEngine: LLMEngine {
    private let apiKey: String
    private let systemPrompt: String
    private let session: URLSession
    
    public init(apiKey: String, systemPrompt: String, session: URLSession = .shared) {
        self.apiKey = apiKey
        self.systemPrompt = systemPrompt
        self.session = session
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        let url = URL(string: "https://api.openai.com/v1/chat/completions")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let openAIRequest = OpenAIRequest(
            model: "gpt-4o-mini",
            messages: [
                OpenAIMessage(role: "system", content: systemPrompt),
                OpenAIMessage(role: "user", content: rawText)
            ],
            responseFormat: OpenAIResponseFormat(type: "json_object")
        )
        
        request.httpBody = try JSONEncoder().encode(openAIRequest)
        
        let (data, response) = try await session.data(for: request)
        
        if let httpResponse = response as? HTTPURLResponse, !(200...299).contains(httpResponse.statusCode) {
            throw NetworkError.serverError(httpResponse.statusCode)
        }
        
        let openAIResponse = try JSONDecoder().decode(OpenAIResponse.self, from: data)
        
        guard let content = openAIResponse.choices.first?.message.content else {
            throw NetworkError.noData
        }
        
        guard let contentData = content.data(using: .utf8) else {
            throw NetworkError.decodingError
        }
        
        do {
            let serializableWorkout = try JSONDecoder().decode(AIWorkoutDTO.self, from: contentData)
            return serializableWorkout.toDomain()
        } catch {
            throw AIParsingError(rawText = rawText, error: "Hallucination: \(error.localizedDescription)")
        }
    }
}

// MARK: - OpenAI DTOs

private struct OpenAIRequest: Codable {
    let model: String
    let messages: [OpenAIMessage]
    let responseFormat: OpenAIResponseFormat
    
    enum CodingKeys: String, CodingKey {
        case model, messages
        case responseFormat = "response_format"
    }
}

private struct OpenAIMessage: Codable {
    let role: String
    let content: String
}

private struct OpenAIResponseFormat: Codable {
    let type: String
}

private struct OpenAIResponse: Codable {
    let choices: [OpenAIChoice]
}

private struct OpenAIChoice: Codable {
    let message: OpenAIMessage
}
