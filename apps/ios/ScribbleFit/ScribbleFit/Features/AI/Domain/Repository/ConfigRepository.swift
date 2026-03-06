import Foundation

public protocol ConfigRepository: Sendable {
    func syncMetadata() async throws
    func syncExercises() async throws
}
