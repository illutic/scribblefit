import Foundation
import SwiftData

@MainActor
public final class LedgerRepositoryImpl: LedgerRepository {
    private let database: ScribbleFitDatabase
    
    public init(database: ScribbleFitDatabase = .shared) {
        self.database = database
    }
    
    public func getWorkoutHistory() async throws -> [WorkoutHistory] {
        let logs = database.getAllWorkoutLogs()
        
        return logs.map { log in
            let sets = database.getSetsForWorkout(id: log.id)
            
            // Map exerciseId to canonical name if possible
            let exerciseGroups = Dictionary(grouping: sets, by: { $0.exerciseId })
            
            let exercises = exerciseGroups.map { (exerciseId, setEntities) in
                let exerciseName = database.searchExercises(query: exerciseId).first?.canonicalName ?? exerciseId
                
                return ExerciseHistory(
                    canonicalName: exerciseName,
                    sets: setEntities.map { 
                        SetHistory(weight: $0.weight, reps: $0.reps, rpe: $0.rpe, notes: $0.notes)
                    }
                )
            }
            
            return WorkoutHistory(
                id: log.id,
                date: log.date,
                location: log.location,
                totalVolume: log.totalVolume,
                exercises: exercises
            )
        }
    }
}
