import Foundation
import SwiftUI
import SwiftData

@MainActor
public final class AppViewModel: ObservableObject {
    private let authRepository: AuthRepository
    private let configRepository: ConfigRepository
    
    @Published public var isInitialized: Bool = false
    
    public init(authRepository: AuthRepository, configRepository: ConfigRepository) {
        self.authRepository = authRepository
        self.configRepository = configRepository
    }
    
    public func initialize() {
        Task {
            // 1. Get/Generate Device ID
            // For iOS we typically use identifierForVendor
            let deviceId = UIDevice.current.identifierForVendor?.uuidString ?? "ios_unknown"
            
            // 2. Perform Login/Auth if needed
            do {
                if try await !authRepository.isLogged() {
                    try await authRepository.login(deviceId: deviceId)
                }
                
                // 3. Sync Metadata & Exercises
                try await configRepository.syncMetadata()
                try await configRepository.syncExercises()
            } catch {
                print("Initialization error: \(error)")
            }
            
            self.isInitialized = true
        }
    }
}
