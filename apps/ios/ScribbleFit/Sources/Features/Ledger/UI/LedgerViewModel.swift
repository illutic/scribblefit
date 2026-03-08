import Foundation
import Combine

@MainActor
public final class LedgerViewModel: ObservableObject {
    @Published public var history: [WorkoutHistory] = []

    private let ledgerRepository: any LedgerRepository

    public init(ledgerRepository: any LedgerRepository) {
        self.ledgerRepository = ledgerRepository
    }

    public func fetchHistory() async {
        history = (try? await ledgerRepository.getWorkoutHistory()) ?? []
    }
}
