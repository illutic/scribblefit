import Foundation

public protocol CanvasRepository: Sendable {
    /**
     * Observes the conversational feed for the current active session.
     */
    func getFeed() async throws -> [FeedItem]

    /**
     * Adds a raw user entry to the persistent sync queue and feed.
     */
    func addScribble(rawText: String) async throws

    /**
     * Retries a failed parsing attempt.
     */
    func retryScribble(id: String) async throws

    /**
     * Adds a structured confirmation card to the feed.
     */
    func addConfirmation(item: ConfirmationItem) async throws

    /**
     * Clears the current canvas feed.
     */
    func clearFeed() async throws
}
