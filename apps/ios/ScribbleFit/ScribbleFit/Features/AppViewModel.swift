import Foundation
import SwiftUI
import SwiftData
public import Combine

@MainActor
public final class AppViewModel: ObservableObject {
    private let configRepository: ConfigRepository

    @Published public var isInitialized: Bool = false

    public init(configRepository: ConfigRepository) {
        self.configRepository = configRepository
    }

    public func initialize() {
        Task {
            // 1. Get/Generate Device ID
            _ = UIDevice.current.identifierForVendor?.uuidString ?? "ios_unknown"

            do {
                // 3. Sync Metadata (prompt config)
                try await configRepository.fetchRemoteConfig()
            } catch {
                print("Initialization error: \(error)")
            }

            self.isInitialized = true
        }
    }
}
