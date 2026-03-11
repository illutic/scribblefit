import Foundation

public struct UserStats: Sendable {
    public let totalWorkouts: Int
    public let lifetimeVolume: Double
    public let prCount: Int
    public let joinDate: Date

    public init(totalWorkouts: Int, lifetimeVolume: Double, prCount: Int = 0, joinDate: Date) {
        self.totalWorkouts = totalWorkouts
        self.lifetimeVolume = lifetimeVolume
        self.prCount = prCount
        self.joinDate = joinDate
    }
}
