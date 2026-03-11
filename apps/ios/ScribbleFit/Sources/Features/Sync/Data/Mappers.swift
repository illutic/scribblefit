import Foundation

extension ScribbleEntity {
    func toScribble() -> Scribble {
        switch syncStatus {
        case .completed:
            if let data = jsonData?.data(using: .utf8),
               let workout = try? JSONDecoder().decode(ParsedWorkout.self, from: data),
               let firstExercise = workout.exercises.first {
                let syncExercise = SyncExercise(
                    canonicalName: firstExercise.canonicalName,
                    muscleGroup: firstExercise.muscleGroup,
                    sets: firstExercise.sets.map { s in
                        SyncExerciseSet(weight: s.weight, reps: s.reps, rpe: s.rpe, notes: s.notes)
                    }
                )
                return .parsed(id: id, createdAt: createdAt, value: syncExercise)
            }
            return .raw(id: id, createdAt: createdAt, rawText: rawText ?? "", status: .pending)
        case .failed:
            return .raw(id: id, createdAt: createdAt, rawText: rawText ?? "", status: .failed)
        case .pending, .processing:
            return .raw(id: id, createdAt: createdAt, rawText: rawText ?? "", status: .pending)
        }
    }
}
