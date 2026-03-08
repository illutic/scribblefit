import Combine
import Foundation

public protocol CanvasRepository: Sendable {
    func getFeed() -> AnyPublisher<[FeedItem], Never>
    func addScribble(rawText: String) async throws
    func retryScribble(id: String) async throws
    func addConfirmation(item: ConfirmationItem) async throws
    func addInsight(item: InsightItem) async throws
    func removeFeedItem(id: String) async throws
    func clearFeed() async throws
}
