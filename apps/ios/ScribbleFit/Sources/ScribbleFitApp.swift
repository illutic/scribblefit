import SwiftUI
#if canImport(UIKit)
import UIKit
#endif

@main
struct ScribbleFitApp: App {
    @StateObject private var navManager = NavigationManager()
    private let database = ScribbleFitDatabase.shared
    private let networkClient = ScribbleFitNetworkClient.shared

    init() {
        #if canImport(UIKit)
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = .white
        appearance.shadowColor = UIColor(red: 0.898, green: 0.898, blue: 0.918, alpha: 1) // LightGray #E5E5EA
        UITabBar.appearance().standardAppearance = appearance
        UITabBar.appearance().scrollEdgeAppearance = appearance
        UITabBar.appearance().tintColor = UIColor(red: 0.063, green: 0.063, blue: 0.063, alpha: 1) // RichBlack #101010
        UITabBar.appearance().unselectedItemTintColor = UIColor(red: 0.557, green: 0.557, blue: 0.627, alpha: 1) // MidGray #8E8EA0
        #endif
    }

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
