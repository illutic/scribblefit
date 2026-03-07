import Foundation
import SwiftUI
import Combine

@MainActor
public final class ProfileViewModel: ObservableObject {
    private let userRepository: UserRepository
    private let navManager: NavigationManager
    
    @Published public var uiState = ProfileUiState()
    
    public init(userRepository: UserRepository, navManager: NavigationManager) {
        self.userRepository = userRepository
        self.navManager = navManager
        
        refreshStats()
    }
    
    public func refreshStats() {
        Task {
            do {
                let stats = try await userRepository.getUserStats()
                self.uiState = ProfileUiState(userName: "George", stats: stats, isLoading: false)
            } catch {
                print("Failed to fetch user stats: \(error)")
                self.uiState.isLoading = false
            }
        }
    }
    
    public func onSettingsClick() {
        navManager.navigate(to: AppDestination.settings, in: .profile)
    }
}

public struct ProfileUiState {
    public var userName: String = "George"
    public var stats: UserStats? = nil
    public var isLoading: Bool = true
    
    public init(userName: String = "George", stats: UserStats? = nil, isLoading: Bool = true) {
        self.userName = userName
        self.stats = stats
        self.isLoading = isLoading
    }
}
