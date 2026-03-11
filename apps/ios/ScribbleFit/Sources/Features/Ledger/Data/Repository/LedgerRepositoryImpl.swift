import Foundation

@MainActor
public final class LedgerRepositoryImpl: LedgerRepository {
    private let database: ScribbleFitDatabase

    public init(database: ScribbleFitDatabase) {
        self.database = database
    }

    public func getWorkoutHistory() async throws -> [WorkoutHistory] {
        let logs = database.getAllWorkoutLogs()
        return logs.map { log in
            let sets = database.getSetsForWorkout(id: log.id)
            let grouped = Dictionary(grouping: sets, by: \.exerciseId)
            let exercises = grouped.map { exerciseId, exerciseSets in
                ExerciseHistory(
                    canonicalName: exerciseId,
                    sets: exerciseSets.map { s in
                        SetHistory(weight: s.weight, reps: s.reps, rpe: s.rpe, notes: s.notes)
                    }
                )
            }
            return WorkoutHistory(
                id: log.id,
                date: log.date,
                location: log.location,
                totalVolume: log.totalVolume ?? 0,
                exercises: exercises
            )
        }
    }

    public func logWorkout(_ workout: WorkoutHistory) async throws {
        let exercises = workout.exercises.map { exercise in
            ExerciseEntity(
                id: exercise.canonicalName,
                canonicalName: exercise.canonicalName,
                muscleGroup: "",
                aliases: []
            )
        }
        database.insertExercisesIfAbsent(exercises)

        database.upsertWorkoutLog(WorkoutEntity(
            id: workout.id,
            date: workout.date,
            location: workout.location,
            totalVolume: workout.totalVolume
        ))

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
