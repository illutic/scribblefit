import SwiftUI

@main
struct ScribbleFitApp: App {
    @StateObject private var navManager = NavigationManager()
    private let database = ScribbleFitDatabase.shared
    private let networkClient = ScribbleFitNetworkClient.shared

    var body: some Scene {
        WindowGroup {
            MainView(
                database: database,
                networkClient: networkClient,
                navManager: navManager
            )
        }
    }
}
