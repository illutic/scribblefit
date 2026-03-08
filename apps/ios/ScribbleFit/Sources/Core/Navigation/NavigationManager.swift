import Foundation
import Combine

public enum AppDestination: Hashable, Sendable {
    case canvas
    case ledger
    case analytics
    case settings
    case exerciseLibrary
}

public enum AppTab: Hashable, Sendable {
    case workout
    case profile
    case ledger
}

@MainActor
public final class NavigationManager: ObservableObject {
    @Published public var workoutPath: [AppDestination] = []
    @Published public var profilePath: [AppDestination] = []
    @Published public var ledgerPath: [AppDestination] = []
    @Published public var selectedTab: AppTab = .workout

    public init() {}

    public func navigate(to destination: AppDestination, in stack: AppTab) {
        switch stack {
        case .workout: workoutPath.append(destination)
        case .profile: profilePath.append(destination)
        case .ledger: ledgerPath.append(destination)
        }
    }

    public func pop(in stack: AppTab) {
        switch stack {
        case .workout: if !workoutPath.isEmpty { workoutPath.removeLast() }
        case .profile: if !profilePath.isEmpty { profilePath.removeLast() }
        case .ledger: if !ledgerPath.isEmpty { ledgerPath.removeLast() }
        }
    }
}
