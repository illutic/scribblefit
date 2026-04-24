import Foundation
import CoreModel
import CoreCommon

public enum LedgerIntent: Sendable {
    case refresh
    case updateDateRange(startDate: Date, endDate: Date)
    case scribbleTapped(id: UUID)
    case exerciseTapped(name: String)
    case dismissDetails
}
