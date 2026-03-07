import SwiftUI
import Combine

public enum AppTab: String, CaseIterable, Identifiable {
    case workout
    case analytics
    case exercises
    case profile
    
    public var id: String { self.rawValue }
    
    public var title: String {
        switch self {
        case .workout: return "Workout"
        case .analytics: return "Analytics"
        case .exercises: return "Exercises"
        case .profile: return "Profile"
        }
    }
    
    public var icon: String {
        switch self {
        case .workout: return "dumbbell.fill"
        case .analytics: return "chart.bar.fill"
        case .exercises: return "list.bullet"
        case .profile: return "person.fill"
        }
    }
}

public enum AppDestination: Hashable {
    case settings
}

@MainActor
public final class NavigationManager: ObservableObject {
    @Published public var activeTab: AppTab = .workout
    @Published public var workoutPath = NavigationPath()
    @Published public var analyticsPath = NavigationPath()
    @Published public var exercisesPath = NavigationPath()
    @Published public var profilePath = NavigationPath()
    
    public init() {}
    
    public func switchTab(to tab: AppTab) {
        activeTab = tab
    }
    
    public func navigate(to destination: any Hashable, in tab: AppTab) {
        switch tab {
        case .workout: workoutPath.append(destination)
        case .analytics: analyticsPath.append(destination)
        case .exercises: exercisesPath.append(destination)
        case .profile: profilePath.append(destination)
        }
    }
    
    public func pop(in tab: AppTab) {
        switch tab {
        case .workout: if !workoutPath.isEmpty { workoutPath.removeLast() }
        case .analytics: if !analyticsPath.isEmpty { analyticsPath.removeLast() }
        case .exercises: if !exercisesPath.isEmpty { exercisesPath.removeLast() }
        case .profile: if !profilePath.isEmpty { profilePath.removeLast() }
        }
    }
}
