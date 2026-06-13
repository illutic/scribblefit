import Foundation
import CoreModel

@MainActor
public final class RemoveExerciseUseCase {
    private let exerciseRepository: ExerciseRepository
    private let scribbleRepository: ScribbleRepository
    
    public init(exerciseRepository: ExerciseRepository, scribbleRepository: ScribbleRepository) {
        self.exerciseRepository = exerciseRepository
        self.scribbleRepository = scribbleRepository
    }
    
    public func execute(id: UUID) async throws {
        guard let exercise = try await exerciseRepository.getExercise(id: id) else { return }
        let scribbleId = exercise.scribbleId
        
        try await exerciseRepository.deleteExercise(id: id)
        
        if let sId = scribbleId {
            if let scribble = try await scribbleRepository.getScribble(id: sId) {
                if scribble.exercises.isEmpty {
                    try await scribbleRepository.deleteScribble(id: sId)
                }
            }
        }
    }
}
