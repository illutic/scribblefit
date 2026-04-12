import Foundation
import SwiftUI
#if SWIFT_PACKAGE
import CoreModel
#endif

public struct LedgerState: Sendable {
    public var startDate: Date
    public var endDate: Date
    public var workouts: [Workout]
    public var isLoading: Bool

    public init(
        startDate: Date = Calendar.current.date(from: Calendar.current.dateComponents([.year, .month], from: .now))!,
        endDate: Date = Calendar.current.date(byAdding: .day, value: -1, to: Calendar.current.date(byAdding: .month, value: 1, to: Calendar.current.date(from: Calendar.current.dateComponents([.year, .month], from: .now))!)!)!,
        workouts: [Workout] = [],
        isLoading: Bool = false
    ) {
        self.startDate = startDate
        self.endDate = endDate
        self.workouts = workouts
        self.isLoading = isLoading
    }

    // Formatting
    public var dateRangeString: String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        return "\(formatter.string(from: startDate)) – \(formatter.string(from: endDate))"
    }

    public var isEmpty: Bool {
        !isLoading && workouts.isEmpty
    }

    public struct GroupedWorkouts: Identifiable, Sendable {
        public let id: String
        public let date: Date
        public let workouts: [Workout]

        public var dateString: String {
            let formatter = DateFormatter()
            formatter.dateFormat = "EEEE, MMMM d"
            return formatter.string(from: date)
        }
    }

    public var groupedWorkouts: [GroupedWorkouts] {
        let calendar = Calendar.current
        let groups = Dictionary(grouping: workouts) { workout in
            calendar.startOfDay(for: workout.date)
        }
        return groups.keys.sorted(by: >).map { date in
            GroupedWorkouts(
                id: date.description,
                date: date,
                workouts: groups[date]!.sorted(by: { $0.date > $1.date })
            )
        }
    }
}
