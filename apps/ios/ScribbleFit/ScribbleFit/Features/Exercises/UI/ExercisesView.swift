import SwiftUI

struct ExercisesView: View {
    var body: some View {
        NavigationStack {
            LedgerView(viewModel: LedgerViewModel(ledgerRepository: LedgerRepositoryImpl()))
                .navigationTitle("Exercises")
                .navigationBarTitleDisplayMode(.inline)
        }
    }
}
