import Foundation
import CoreModel
import CoreCommon

public struct LedgerState: Equatable, Sendable {
    public var scribbles: [Scribble]
    public var isLoading: Bool
    public var startDate: Date
    public var endDate: Date
    public var navigationState: NavigationState?
    public var weightUnit: WeightUnit

    private static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        return formatter
    }()

    public var dateRangeString: String {
        return "\(Self.dateFormatter.string(from: startDate)) - \(Self.dateFormatter.string(from: endDate))"
    }

    public enum NavigationState: Equatable, Sendable, Identifiable {
        case scribbleDetails(UUID)
        case exerciseDetails(String)

        public var id: String {
            switch self {
            case .scribbleDetails(let uuid):
                return "scribble-\(uuid.uuidString)"
            case .exerciseDetails(let name):
                return "exercise-\(name)"
            }
        }
    }

    public init(
        scribbles: [Scribble] = [],
        isLoading: Bool = true,
        startDate: Date = Calendar.current.date(byAdding: .month, value: -1, to: Date())!,
        endDate: Date = Date(),
        navigationState: NavigationState? = nil,
        weightUnit: WeightUnit = .kgs
    ) {
        self.scribbles = scribbles
        self.isLoading = isLoading
        self.startDate = startDate
        self.endDate = endDate
        self.navigationState = navigationState
        self.weightUnit = weightUnit
    }

    public func copy(
        scribbles: [Scribble]? = nil,
        isLoading: Bool? = nil,
        startDate: Date? = nil,
        endDate: Date? = nil,
        navigationState: NavigationState?? = nil,
        weightUnit: WeightUnit? = nil
    ) -> LedgerState {
        var newState = self
        if let scribbles = scribbles { newState.scribbles = scribbles }
        if let isLoading = isLoading { newState.isLoading = isLoading }
        if let startDate = startDate { newState.startDate = startDate }
        if let endDate = endDate { newState.endDate = endDate }
        if let navigationState = navigationState { newState.navigationState = navigationState }
        if let weightUnit = weightUnit { newState.weightUnit = weightUnit }
        return newState
    }

    public var isEmpty: Bool {
        !isLoading && scribbles.isEmpty
    }

    public struct GroupedScribbles: Identifiable, Sendable {
        public var id: Date { date }
        public let date: Date
        public let scribbles: [Scribble]
    }

    public var groupedScribbles: [GroupedScribbles] {
        let calendar = Calendar.current
        let groups = Dictionary(grouping: scribbles) { scribble in
            calendar.startOfDay(for: scribble.createdAt)
        }
        
        return groups.keys.sorted(by: >).map { date in
            let dayScribbles = groups[date]!.sorted(by: { $0.createdAt < $1.createdAt })
            return GroupedScribbles(
                date: date,
                scribbles: dayScribbles
            )
        }
    }
}
