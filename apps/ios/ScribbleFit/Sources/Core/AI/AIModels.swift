import Foundation

public struct ParsedWorkout: Codable, Sendable {
    public let date: String
    public let location: String?
    public let exercises: [ParsedExercise]

    public init(date: String, location: String? = nil, exercises: [ParsedExercise]) {
        self.date = date
        self.location = location
        self.exercises = exercises
    }
}

public struct ParsedExercise: Codable, Sendable {
    public let canonicalName: String
    public let muscleGroup: String
    public let sets: [ParsedSet]

    enum CodingKeys: String, CodingKey {
        case canonicalName = "canonical_name"
        case muscleGroup = "muscle_group"
        case sets
    }

    public init(canonicalName: String, muscleGroup: String, sets: [ParsedSet]) {
        self.canonicalName = canonicalName
        self.muscleGroup = muscleGroup
        self.sets = sets
    }
}

public struct ParsedSet: Codable, Sendable {
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

public enum ParsingStatus: Sendable { case success, partialSuccess, failure }

public struct ParsedWorkoutResult: Sendable {
    public let workout: ParsedWorkout?
    public let rawText: String
    public let status: ParsingStatus
    public let modelUsed: String?
    public let processingTimeMs: Int64
    public let error: String?

    public init(workout: ParsedWorkout?, rawText: String, status: ParsingStatus, modelUsed: String? = nil, processingTimeMs: Int64 = 0, error: String? = nil) {
        self.workout = workout
        self.rawText = rawText
        self.status = status
        self.modelUsed = modelUsed
        self.processingTimeMs = processingTimeMs
        self.error = error
    }
}

public struct AISyncItem: Sendable, Identifiable {
    public let id: String
    public let itemType: String
    public let rawText: String?
    public let status: SyncStatus
    public let createdAt: Date
    public let jsonData: String?

    public init(id: String, itemType: String, rawText: String?, status: SyncStatus, createdAt: Date, jsonData: String?) {
        self.id = id
        self.itemType = itemType
        self.rawText = rawText
        self.status = status
        self.createdAt = createdAt
        self.jsonData = jsonData
    }
}

public enum LLMProvider: String, Codable, Sendable {
    case openai, gemini, local, proxy
}

public struct SystemConfigDomain: Sendable {
    public let promptVersion: String
    public let promptText: String
    public let exerciseVersion: String
    public let preferredLlmProvider: LLMProvider
    public let preferredModel: String
    public let parsingMode: String
    public let weightUnit: String
    public let themePreference: String
    public let updatedAt: Date

    public init(promptVersion: String, promptText: String, exerciseVersion: String = "0.0.0", preferredLlmProvider: LLMProvider = .proxy, preferredModel: String = "", parsingMode: String = "managed", weightUnit: String = "lbs", themePreference: String = "system", updatedAt: Date = Date()) {
        self.promptVersion = promptVersion
        self.promptText = promptText
        self.exerciseVersion = exerciseVersion
        self.preferredLlmProvider = preferredLlmProvider
        self.preferredModel = preferredModel
        self.parsingMode = parsingMode
        self.weightUnit = weightUnit
        self.themePreference = themePreference
        self.updatedAt = updatedAt
    }
}
