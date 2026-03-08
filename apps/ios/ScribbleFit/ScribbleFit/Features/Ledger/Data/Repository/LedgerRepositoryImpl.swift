import Foundation
import SwiftData

@MainActor
public final class LedgerRepositoryImpl: LedgerRepository {
    private let database: ScribbleFitDatabase
    
    public init(database: ScribbleFitDatabase) {
        self.database = database
    }
    
    @MainActor
    public convenience init() {
        self.init(database: .shared)
    }
    
    public func getWorkoutHistory() async throws -> [WorkoutHistory] {
        let logs = await database.getAllWorkoutLogs()
        var histories: [WorkoutHistory] = []
        histories.reserveCapacity(logs.count)

        for log in logs {
            let sets = await database.getSetsForWorkout(id: log.id)
            let exerciseGroups = Dictionary(grouping: sets, by: { $0.exerciseId })

            let exercises: [ExerciseHistory] = exerciseGroups.map { (exerciseId, setEntities) in
                let exerciseName = database.searchExercises(query: exerciseId).first?.canonicalName ?? exerciseId
                return ExerciseHistory(
                    canonicalName: exerciseName,
                    sets: setEntities.map {
                        SetHistory(weight: $0.weight, reps: $0.reps, rpe: $0.rpe, notes: $0.notes)
                    }
                )
            }

            let history = WorkoutHistory(
                id: log.id,
                date: log.date,
                location: log.location,
                totalVolume: log.totalVolume ?? 0.0,
                exercises: exercises
            )
            histories.append(history)
        }

        return histories
    }

    public func logWorkout(_ workout: WorkoutHistory) async throws {
        let log = WorkoutLog(
            id: workout.id,
            date: workout.date,
            location: workout.location,
            totalVolume: workout.totalVolume
        )
        await database.upsertWorkoutLog(log)
        
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
        await database.upsertWorkoutSets(sets)
    }
}
