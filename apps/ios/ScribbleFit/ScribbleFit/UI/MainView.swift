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
                    Label("Canvas", systemImage: "pencil.and.outline")
                }
            
            LedgerView(viewModel: LedgerViewModel(ledgerRepository: ledgerRepository))
                .tabItem {
                    Label("Ledger", systemImage: "list.bullet")
                }
        }
        .accentColor(Color(hex: "101010"))
        // SwiftUI doesn't easily support pure white tab bars without extra setup, 
        // but this aligns with the minimalist mandate.
    }
}
