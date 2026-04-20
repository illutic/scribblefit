import Foundation

public enum LedgerIntent: Sendable {
    case refresh
    case updateDateRange(startDate: Date, endDate: Date)
    case workoutTapped(id: UUID)
}
