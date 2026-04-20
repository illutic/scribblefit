import Foundation

@MainActor
public protocol ExerciseRepository: Sendable {
    func getExercises(query: String) async throws -> [Exercise]
    func saveExercise(_ exercise: Exercise) async throws
    func deleteExercise(id: UUID) async throws
}
