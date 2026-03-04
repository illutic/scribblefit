import Foundation

public final class GeminiAIEngine: LLMEngine {
    private let apiKey: String
    private let systemPrompt: String
    private let session: URLSession
    
    public init(apiKey: String, systemPrompt: String, session: URLSession = .shared) {
        self.apiKey = apiKey
        self.systemPrompt = systemPrompt
        self.session = session
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        let url = URL(string: "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=\(apiKey)")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let geminiRequest = GeminiRequest(
            contents: [
                GeminiContent(parts: [GeminiPart(text: rawText)])
            ],
            systemInstruction: GeminiSystemInstruction(
                parts: [GeminiPart(text: systemPrompt)]
            ),
            generationConfig: GeminiGenerationConfig(responseMimeType: "application/json")
        )
        
        request.httpBody = try JSONEncoder().encode(geminiRequest)
        
        let (data, response) = try await session.data(for: request)
        
        if let httpResponse = response as? HTTPURLResponse, !(200...299).contains(httpResponse.statusCode) {
            throw NetworkError.serverError(httpResponse.statusCode)
        }
        
        let geminiResponse = try JSONDecoder().decode(GeminiResponse.self, from: data)
        
        guard let content = geminiResponse.candidates.first?.content.parts.first?.text else {
            throw NetworkError.noData
        }
        
        guard let contentData = content.data(using: .utf8) else {
            throw NetworkError.decodingError
        }
        
        let serializableWorkout = try JSONDecoder().decode(ParsedWorkoutSerializable.self, from: contentData)
        return serializableWorkout.toDomain()
    }
}

// MARK: - Gemini DTOs

private struct GeminiRequest: Codable {
    let contents: [GeminiContent]
    let systemInstruction: GeminiSystemInstruction
    let generationConfig: GeminiGenerationConfig
}

private struct GeminiContent: Codable {
    let parts: [GeminiPart]
}

private struct GeminiSystemInstruction: Codable {
    let parts: [GeminiPart]
}

private struct GeminiPart: Codable {
    let text: String
}

private struct GeminiGenerationConfig: Codable {
    let responseMimeType: String
}

private struct GeminiResponse: Codable {
    let candidates: [GeminiCandidate]
}

private struct GeminiCandidate: Codable {
    let content: GeminiContent
}

// MARK: - Serializable Mapping (Local to Engine)

private struct ParsedWorkoutSerializable: Codable {
    let date: String
    let location: String?
    let exercises: [ParsedExerciseSerializable]
    
    func toDomain() -> ParsedWorkout {
        return ParsedWorkout(
            date: self.date,
            location: self.location,
            exercises: self.exercises.map { $0.toDomain() }
        )
    }
}

private struct ParsedExerciseSerializable: Codable {
    let canonicalName: String
    let sets: [ParsedSetSerializable]
    
    func toDomain() -> ParsedExercise {
        return ParsedExercise(
            canonicalName: self.canonicalName,
            sets: self.sets.map { $0.toDomain() }
        )
    }
}

private struct ParsedSetSerializable: Codable {
    let weight: Double
    let reps: Int
    let rpe: Double?
    let notes: String?
    
    func toDomain() -> ParsedSet {
        return ParsedSet(
            weight: self.weight,
            reps: self.reps,
            rpe: self.rpe,
            notes: self.notes
        )
    }
}
