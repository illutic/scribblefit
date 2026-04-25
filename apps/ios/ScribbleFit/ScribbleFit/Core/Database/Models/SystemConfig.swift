import Foundation
import SwiftData

@Model
public final class SystemConfig {
    @Attribute(.unique) public var id: String
    public var promptVersion: String
    public var promptText: String
    public var exerciseVersion: String
    public var preferredLlmProvider: String
    public var preferredModel: String
    public var weightUnit: String
    public var themePreference: String
    public var updatedAt: Date

    public init(
        id: String = "config",
        promptVersion: String,
        promptText: String = defaultPrompt,
        exerciseVersion: String = "0.0.0",
        preferredLlmProvider: String = "gemini",
        preferredModel: String = "",
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
        self.weightUnit = weightUnit
        self.themePreference = themePreference
        self.updatedAt = updatedAt
    }
    
    public static let defaultPrompt = """
        You are ScribbleFit AI, a fitness parsing assistant. 
        Your goal is to take raw, messy gym shorthand and parse it into a structured JSON format.
        
        Strictly follow this JSON schema:
        {
          "date": "YYYY-MM-DD",
          "location": "String or null",
          "exercises": [
            {
              "canonical_name": "String",
              "sets": [
                {
                  "weight": number,
                  "reps": integer,
                  "rpe": number or null,
                  "notes": "String or null"
                }
              ]
            }
          ]
        }
        
        Always output a clean valid JSON code. Do not include any other information. Do NOT use any characters that may break the json format.
    """
}
