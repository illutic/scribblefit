import Foundation

#if canImport(FoundationModels)
import FoundationModels
#endif

/**
 * Local AI Engine for iOS.
 * Leverages Apple Intelligence via the FoundationModels framework.
 */
public final class LocalAIEngine: LLMEngine {
    private let systemPrompt: String
    
    public init(systemPrompt: String) {
        self.systemPrompt = systemPrompt
    }
    
    public func parseWorkout(rawText: String) async throws -> ParsedWorkout {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            // Ensure Apple Intelligence is available
            guard await isAvailable() else {
                throw AIParsingError(rawText: rawText, error: "Apple Intelligence not available")
            }
            
            let session = LanguageModelSession {
                self.systemPrompt
            }
            
            do {
                // Use Guided Generation for structured output
                let response = try await session.respond(
                    to: "Parse this gym note: \(rawText)",
                    generating: LocalAIWorkoutDTO.self
                )
                
                return response.content.toDomain()
            } catch {
                throw AIParsingError(rawText: rawText, error: "Local Hallucination: \(error.localizedDescription)")
            }
        }
        #endif
        
        throw AIParsingError(rawText: rawText, error: "Local AI Engine not supported on this device/OS")
    }
    
    public func isAvailable() async -> Bool {
        #if canImport(FoundationModels)
        if #available(iOS 26.0, *) {
            return SystemLanguageModel.default.availability == .available
        }
        #endif
        return false
    }
}

// MARK: - Serializable Mapping (Internal to Engine)

#if canImport(FoundationModels)
@available(iOS 26.0, *)
@Generable
struct LocalAIWorkoutDTO {
    @Guide(description: "ISO8601 date string of the workout")
    let date: String
    
    @Guide(description: "Optional location of the workout")
    let location: String?
    
    @Guide(description: "List of exercises performed")
    let exercises: [LocalAIExerciseDTO]
    
    func toDomain() -> ParsedWorkout {
        return ParsedWorkout(
            date: self.date,
            location: self.location,
            exercises: self.exercises.map { $0.toDomain() }
        )
    }
}

@available(iOS 26.0, *)
@Generable
struct LocalAIExerciseDTO {
    @Guide(description: "The canonical name of the exercise")
    let canonicalName: String
    
    @Guide(description: "List of sets performed for this exercise")
    let sets: [LocalAISetDTO]
    
    func toDomain() -> ParsedExercise {
        return ParsedExercise(
            canonicalName: self.canonicalName,
            sets: self.sets.map { $0.toDomain() }
        )
    }
}

@available(iOS 26.0, *)
@Generable
struct LocalAISetDTO {
    @Guide(description: "The weight used in lbs or kg")
    let weight: Double
    
    @Guide(description: "Number of repetitions")
    let reps: Int
    
    @Guide(description: "Rate of Perceived Exertion (1-10)")
    let rpe: Double?
    
    @Guide(description: "Any extra notes for this set")
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
#endif
