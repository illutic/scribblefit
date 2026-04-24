import Foundation
import CoreModel

@MainActor
public final class GetWorkoutsInRangeUseCase {
    private let repository: ScribbleRepository
    
    public init(repository: ScribbleRepository) {
        self.repository = repository
    }
    
    public func execute(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> {
        let stream = repository.observeScribbles(startDate: startDate, endDate: endDate)
        
        return AsyncStream { continuation in
            Task {
                for await scribbles in stream {
                    // Only show completed scribbles in the Ledger (history)
                    let completedScribbles = scribbles.filter { $0.status == .completed }
                    continuation.yield(completedScribbles)
                }
            }
        }
    }
}
