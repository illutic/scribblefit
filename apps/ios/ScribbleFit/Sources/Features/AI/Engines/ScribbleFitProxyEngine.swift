import Foundation

public final class ScribbleFitProxyEngine: LLMEngine {
    private let client: ScribbleFitNetworkClient
    private let systemPrompt: String
    
    public init(client: ScribbleFitNetworkClient = .shared, systemPrompt: String) {
        self.client = client
        self.systemPrompt = systemPrompt
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        let request = ParseRequest(rawText: rawText, prompt: systemPrompt)
        let dto = try await client.parseProxy(request: request)
        return mapToDomain(dto)
    }
    
    private func mapToDomain(_ dto: ParsedWorkoutDto) -> ParsedWorkout {
        return ParsedWorkout(
            date: dto.date,
            location: dto.location,
            exercises: dto.exercises.map { exerciseDto in
                ParsedExercise(
                    canonicalName: exerciseDto.canonicalName,
                    sets: exerciseDto.sets.map { setDto in
                        ParsedSet(
                            weight: setDto.weight,
                            reps: setDto.reps,
                            rpe: setDto.rpe,
                            notes: setDto.notes
                        )
                    }
                )
            }
        )
    }
}
