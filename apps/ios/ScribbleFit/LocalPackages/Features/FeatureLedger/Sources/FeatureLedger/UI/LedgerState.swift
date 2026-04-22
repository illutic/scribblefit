import Foundation
import SwiftUI
import CoreModel

public struct LedgerState: Sendable {
    public var startDate: Date
    public var endDate: Date
    public var workouts: [Workout]
    public var isLoading: Bool
    public var weightUnit: WeightUnit
    public var navigationState: NavigationState? = nil

    public enum NavigationState: Equatable, Sendable, Identifiable {
        case exerciseDetails(String)
        case workoutExercises(UUID)

        public var id: String {
            switch self {
            case .exerciseDetails(let name):
                return "exercise-\(name)"
            case .workoutExercises(let uuid):
                return "workout-\(uuid.uuidString)"
            }
        }
    }

    public init(
        startDate: Date = Calendar.current.date(byAdding: .day, value: -30, to: Date())!,
        endDate: Date = Date(),
        workouts: [Workout] = [],
        isLoading: Bool = false,
        weightUnit: WeightUnit = .kgs
    ) {
        self.startDate = startDate
        self.endDate = endDate
        self.workouts = workouts
        self.isLoading = isLoading
        self.weightUnit = weightUnit
    }

    public func copy(
        startDate: Date? = nil,
        endDate: Date? = nil,
        workouts: [Workout]? = nil,
        isLoading: Bool? = nil,
        weightUnit: WeightUnit? = nil,
        navigationState: NavigationState?? = nil
    ) -> LedgerState {
        var newState = self
        if let startDate = startDate { newState.startDate = startDate }
        if let endDate = endDate { newState.endDate = endDate }
        if let workouts = workouts { newState.workouts = workouts }
        if let isLoading = isLoading { newState.isLoading = isLoading }
        if let weightUnit = weightUnit { newState.weightUnit = weightUnit }
        if let navigationState = navigationState { newState.navigationState = navigationState }
        return newState
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
        public let exercises: [Exercise]

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
            let dayWorkouts = groups[date]!.sorted(by: { $0.date < $1.date })
            let dayExercises = dayWorkouts.flatMap { $0.exercises }

            return GroupedWorkouts(
                id: date.description,
                date: date,
                workouts: dayWorkouts,
                exercises: dayExercises
            )
        }
    }
}
