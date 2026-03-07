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
                totalVolume: log.totalVolume ?? 0.0,
                exercises: exercises
            )
        }
    }

    public func logWorkout(_ workout: WorkoutHistory) async throws {
        let log = WorkoutLog(
            id: workout.id,
            date: workout.date,
            location: workout.location,
            totalVolume: workout.totalVolume
        )
        database.upsertWorkoutLog(log)
        
        let sets = workout.exercises.flatMap { exercise in
            exercise.sets.map { set in
                WorkoutSet(
                    id: UUID().uuidString,
                    weight: set.weight,
                    reps: set.reps,
                    rpe: set.rpe,
                    notes: set.notes,
                    exerciseId: exercise.canonicalName
                )
            }
        }
        database.upsertWorkoutSets(sets)
    }
}
