import Foundation
import CoreModel

public enum ScribbleDetailsIntent: Sendable {
    case loadScribble(UUID)
    case logScribble
    case dismiss
    case exerciseTapped(name: String)
    case dismissExerciseDetails
}
