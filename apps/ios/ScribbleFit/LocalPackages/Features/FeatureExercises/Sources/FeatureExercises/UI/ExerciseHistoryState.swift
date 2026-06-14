import Foundation
import CoreModel

public struct ExerciseHistoryState: Equatable {
    public var exerciseName: String
    public var isLoading: Bool = true
    public var history: [ExerciseHistorySession] = []
    public var error: String?

    private static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM yyyy"
        return formatter
    }()

    // Grouping by Month Year
    public var groupedHistory: [(String, [ExerciseHistorySession])] {
        let formatter = Self.dateFormatter

        let grouped = Dictionary(grouping: history) { session in
            let date = session.date
            return formatter.string(from: date).uppercased()
        }

        // Sorting the keys could be complex, but history is already sorted chronologically,
        // so we can just use the order of appearance or sort by the first element's date.
        let sortedKeys = grouped.keys.sorted { key1, key2 in
            let firstDate1 = grouped[key1]?.first?.date ?? Date.distantPast
            let firstDate2 = grouped[key2]?.first?.date ?? Date.distantPast
            return firstDate1 > firstDate2
        }

        return sortedKeys.map { ($0, grouped[$0]!) }
    }

    public init(exerciseName: String) {
        self.exerciseName = exerciseName
    }
}
