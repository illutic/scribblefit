import Foundation
import SwiftData

@Model public final class ExerciseDictionary {
    @Attribute(.unique) public var id: String
    public var canonicalName: String
    public var muscleGroup: String
    public var aliases: [String]

    public init(id: String, canonicalName: String, muscleGroup: String = "", aliases: [String] = []) {
        self.id = id
        self.canonicalName = canonicalName
        self.muscleGroup = muscleGroup
        self.aliases = aliases
    }
}
