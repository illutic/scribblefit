import Foundation

@MainActor
public protocol ExerciseRepository: Sendable {
    func getExercises(query: String) async throws -> [Exercise]
    func observeExercises(query: String) -> AsyncStream<[Exercise]>
    func getExercise(id: UUID) async throws -> Exercise?
    func saveExercise(_ exercise: Exercise) async throws
    func saveExercise(_ exercise: Exercise, to scribbleId: UUID) async throws
    func updateExercise(_ exercise: Exercise) async throws
    func deleteExercise(id: UUID) async throws
}
