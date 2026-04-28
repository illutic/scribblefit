import Foundation
import CoreModel
import CoreCommon

/**
 * Use case to fetch the complete history of a specific exercise.
 */
@MainActor
public final class GetExerciseHistoryUseCase {
    private let exerciseRepository: ExerciseRepository
    private let formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase
    
    public init(
        exerciseRepository: ExerciseRepository,
        formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase
    ) {
        self.exerciseRepository = exerciseRepository
        self.formatExerciseSummaryUseCase = formatExerciseSummaryUseCase
    }
    
    public func execute(
        exerciseName: String,
        weightUnit: WeightUnit
    ) async throws -> [ExerciseHistorySession] {
        let history = try await exerciseRepository.getExercises(query: exerciseName)
            .filter { $0.canonicalName.lowercased() == exerciseName.lowercased() }
            .sorted(by: { $0.createdAt > $1.createdAt })
        
        if history.isEmpty { return [] }
        
        // Calculate max weight and volume across all time to identify PBs
        let allTimeMaxWeight = history.flatMap { $0.sets }.compactMap { $0.weight }.max() ?? 0.0
        let allTimeMaxVolume = history.map { exercise in
            exercise.sets.reduce(Float(0.0)) { $0 + Calculations.calculateVolume(weight: $1.weight, reps: $1.reps) }
        }.max() ?? 0.0
        
        return history.map { exercise in
            let sessionVolume = exercise.sets.reduce(Float(0.0)) { $0 + Calculations.calculateVolume(weight: $1.weight, reps: $1.reps) }
            let sessionMaxWeight = exercise.sets.compactMap { $0.weight }.max() ?? 0.0
            
            let isPB = (sessionMaxWeight >= allTimeMaxWeight && allTimeMaxWeight > 0.0) ||
                       (sessionVolume >= allTimeMaxVolume && allTimeMaxVolume > 0.0)
            
            return ExerciseHistorySession(
                exercise: exercise,
                totalVolume: sessionVolume,
                maxWeight: sessionMaxWeight,
                summary: formatExerciseSummaryUseCase.execute(exercise: exercise, weightUnit: weightUnit),
                isPersonalBest: isPB,
                scribbleId: exercise.scribbleId ?? UUID()
            )
        }
    }
}
