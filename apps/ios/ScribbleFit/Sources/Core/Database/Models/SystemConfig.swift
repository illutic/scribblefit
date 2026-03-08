import Foundation
import SwiftData

@Model public final class SystemConfig {
    public var id: String
    public var promptVersion: String
    public var promptText: String
    public var exerciseVersion: String
    public var preferredLlmProvider: String
    public var preferredModel: String
    public var parsingMode: String
    public var weightUnit: String
    public var themePreference: String
    public var updatedAt: Date

    public static let defaultPrompt = """
        You are ScribbleFit AI, a fitness parsing assistant.
        Parse raw gym shorthand into this JSON schema:
        {"date":"YYYY-MM-DD","location":"String or null","exercises":[{"canonical_name":"String","muscle_group":"String","sets":[{"weight":number,"reps":integer,"rpe":number|null,"notes":"String|null"}]}]}
        Output ONLY valid JSON. No markdown, no extra text.
        """

    public init(
        id: String = "config",
        promptVersion: String = "1.0.0",
        promptText: String = SystemConfig.defaultPrompt,
        exerciseVersion: String = "0.0.0",
        preferredLlmProvider: String = "proxy",
        preferredModel: String = "",
        parsingMode: String = "managed",
        weightUnit: String = "lbs",
        themePreference: String = "system",
        updatedAt: Date = Date()
    ) {
        self.id = id
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
