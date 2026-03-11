import Foundation

public enum Scribble: Sendable, Identifiable {
    case raw(id: String, createdAt: Date, rawText: String, status: ScribbleSyncStatus)
    case parsed(id: String, createdAt: Date, value: SyncExercise)
    case insight(id: String, createdAt: Date, displayText: String, textValue: String)

    public var id: String {
        switch self {
        case .raw(let id, _, _, _): return id
        case .parsed(let id, _, _): return id
        case .insight(let id, _, _, _): return id
        }
    }

    public var createdAt: Date {
        switch self {
        case .raw(_, let createdAt, _, _): return createdAt
        case .parsed(_, let createdAt, _): return createdAt
        case .insight(_, let createdAt, _, _): return createdAt
        }
    }
}

public enum ScribbleSyncStatus: Sendable, Equatable {
    case pending
    case failed
    case logged
    case completed(parsedResult: ParsedWorkoutResult)

    public static func == (lhs: ScribbleSyncStatus, rhs: ScribbleSyncStatus) -> Bool {
        switch (lhs, rhs) {
        case (.pending, .pending), (.failed, .failed), (.logged, .logged): return true
        case (.completed, .completed): return true
        default: return false
        }
    }
}

public struct SyncExercise: Sendable {
    public let canonicalName: String
    public let muscleGroup: String
    public let sets: [SyncExerciseSet]

    public init(canonicalName: String, muscleGroup: String, sets: [SyncExerciseSet]) {
        self.canonicalName = canonicalName
        self.muscleGroup = muscleGroup
        self.sets = sets
    }
}

public struct SyncExerciseSet: Sendable {
    public let weight: Double
    public let reps: Int
    public let rpe: Double?
    public let notes: String?

    public init(weight: Double, reps: Int, rpe: Double? = nil, notes: String? = nil) {
        self.weight = weight
        self.reps = reps
        self.rpe = rpe
        self.notes = notes
    }
}
