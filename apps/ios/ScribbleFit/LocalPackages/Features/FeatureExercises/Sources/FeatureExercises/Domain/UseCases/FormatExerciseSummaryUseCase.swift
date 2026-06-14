import Foundation
import CoreModel

public struct FormatExerciseSummaryUseCase: Sendable {
    public init() {}

    public func execute(exercise: Exercise, weightUnit: WeightUnit) -> String {
        if exercise.sets.isEmpty { return "" }

        let unitLabel = weightUnit == .kgs ? "kg" : "lb"

        // Group consecutive identical sets
        struct SetGroup {
            let weight: Float?
            let reps: Int
            var count: Int
        }
        var groups: [SetGroup] = []

        for workoutSet in exercise.sets {
            if let lastIndex = groups.indices.last,
               groups[lastIndex].weight == workoutSet.weight,
               groups[lastIndex].reps == workoutSet.reps {
                groups[lastIndex].count += 1
            } else {
                groups.append(SetGroup(weight: workoutSet.weight, reps: workoutSet.reps, count: 1))
            }
        }

        func formatWeight(_ weight: Float?) -> String {
            if let weight = weight {
                return String(format: "%.1f%@", weight, unitLabel)
            } else {
                return String(localized: "Bodyweight")
            }
        }

        if groups.count == 1 {
            let group = groups[0]
            // Standard format for uniform sets: "100.0kg • 3 sets x 10 reps"
            return "\(formatWeight(group.weight)) • \(group.count) \(String(localized: "sets")) x \(group.reps) \(String(localized: "reps"))"
        } else {
            // Format for varied sets: "100.0kg 3x10, 90.0kg 2x8"
            return groups.map { group in
                "\(formatWeight(group.weight)) \(group.count)x\(group.reps)"
            }.joined(separator: ", ")
        }
    }
}
