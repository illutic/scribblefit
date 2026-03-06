import Foundation
import SwiftUI
public import Combine

@MainActor
public final class LedgerViewModel: ObservableObject {
    private let ledgerRepository: LedgerRepository
    
    @Published public var history: [WorkoutHistory] = []
    
    public init(ledgerRepository: LedgerRepository) {
        self.ledgerRepository = ledgerRepository
    }
    
    public func fetchHistory() {
        Task {
            do {
                self.history = try await ledgerRepository.getWorkoutHistory()
            } catch {
                print("Failed to fetch history: \(error)")
            }
        }
    }
}
