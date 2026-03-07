import SwiftUI

public struct MainView: View {
    // In a real DI system, these would be injected or retrieved from a container
    private let syncRepository: SyncRepository
    private let ledgerRepository: LedgerRepository
    
    public init(syncRepository: SyncRepository, ledgerRepository: LedgerRepository) {
        self.syncRepository = syncRepository
        self.ledgerRepository = ledgerRepository
    }
    
    public var body: some View {
        TabView {
            CanvasView(viewModel: CanvasViewModel(syncRepository: syncRepository))
            .tabItem {
                Label("Workout", systemImage: "dumbbell.fill")
            }
            
            Text("Analytics").tabItem { Label("Analytics", systemImage: "chart.bar.fill") }
            Text("Exercises").tabItem { Label("Exercises", systemImage: "list.bullet") }
            Text("Profile").tabItem { Label("Profile", systemImage: "person.fill") }
        }
        .accentColor(ScribbleFitColor.primaryText)
    }
}
