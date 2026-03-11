import Foundation
import SwiftData

@Model public final class WorkoutEntity {
    public var id: String
    public var date: Date
    public var location: String?
    public var totalVolume: Double?
    @Relationship(deleteRule: .cascade, inverse: \WorkoutSet.workout)
    public var sets: [WorkoutSet]?

    public init(id: String, date: Date, location: String? = nil, totalVolume: Double? = nil) {
        self.id = id
        self.date = date
        self.location = location
        self.totalVolume = totalVolume
    }
}
