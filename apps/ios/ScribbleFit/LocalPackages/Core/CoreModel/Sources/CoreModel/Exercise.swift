import Foundation

public struct Exercise: Identifiable, Equatable, Sendable, Codable {
    public let id: UUID
    public let canonicalName: String
    public let muscleGroup: String
    public let sets: [ExerciseSet]
    public let isDraft: Bool
    public let estimated1RM: Float?
    public let intensity: Float?

    public init(
        id: UUID = UUID(),
        canonicalName: String,
        muscleGroup: String,
        sets: [ExerciseSet] = [],
        isDraft: Bool = false,
        estimated1RM: Float? = nil,
        intensity: Float? = nil
    ) {
        self.id = id
        self.canonicalName = canonicalName
        self.muscleGroup = muscleGroup
        self.sets = sets
        self.isDraft = isDraft
        self.estimated1RM = estimated1RM
        self.intensity = intensity
    }

    public func summary(weightUnit: WeightUnit) -> String {
        guard !sets.isEmpty else { return "" }
        
        let unitLabel = weightUnit == .kgs ? "kg" : "lbs"
        
        // Group consecutive identical sets
        var groups: [(weight: Float?, reps: Int, count: Int)] = []
        for set in sets {
            if let last = groups.last, last.weight == set.weight, last.reps == set.reps {
                groups[groups.count - 1].count += 1
            } else {
                groups.append((weight: set.weight, reps: set.reps, count: 1))
            }
        }
        
        func formatWeight(_ weight: Float?) -> String {
            if let weight = weight {
                return "\(String(format: "%.1f", weight))\(unitLabel)"
            } else {
                return String(localized: "Bodyweight")
            }
        }
        
        if groups.count == 1 {
            let group = groups[0]
            // Standard format for uniform sets: "100.0kg • 3 sets x 10 reps"
            return "\(formatWeight(group.weight)) • \(group.count) sets x \(group.reps) reps"
        } else {
            // Format for varied sets: "100.0kg 3x10, 90.0kg 2x8"
            return groups.map { group in
                "\(formatWeight(group.weight)) \(group.count)x\(group.reps)"
            }.joined(separator: ", ")
        }
    }
}
