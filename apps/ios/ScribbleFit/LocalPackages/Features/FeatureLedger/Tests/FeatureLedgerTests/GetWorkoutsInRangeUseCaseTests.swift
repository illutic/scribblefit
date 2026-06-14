import XCTest
import CoreModel
@testable import FeatureLedger

// MARK: - Mock

@MainActor
private final class MockScribbleRepository: ScribbleRepository {
    var scribblesToStream: [Scribble] = []

    func observeScribbles(for date: Date) -> AsyncStream<[Scribble]> {
        let data = scribblesToStream
        return AsyncStream { cont in cont.yield(data); cont.finish() }
    }
    func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> {
        let data = scribblesToStream
        return AsyncStream { cont in cont.yield(data); cont.finish() }
    }
    func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]> {
        let data = scribblesToStream
        return AsyncStream { cont in cont.yield(data); cont.finish() }
    }
    func addScribble(_ scribble: Scribble) async throws {}
    func updateScribble(_ scribble: Scribble) async throws {}
    func deleteScribble(id: UUID) async throws {}
    func getScribble(id: UUID) async throws -> Scribble? { nil }
    func clearScribbleExercises(scribbleId: UUID) async throws {}
    func confirmScribble(_ scribble: Scribble) async throws {}
}

// MARK: - Helpers

private func makeScribble(status: ScribbleStatus, date: Date = Date()) -> Scribble {
    Scribble(id: UUID(), rawText: "test", status: status, createdAt: date)
}

// MARK: - GetWorkoutsInRangeUseCase Tests

@MainActor
final class GetWorkoutsInRangeUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepository!
    private var sut: GetWorkoutsInRangeUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockScribbleRepository()
        sut = GetWorkoutsInRangeUseCase(repository: mockRepo)
    }

    // MARK: - Happy Path

    func test_execute_returnsOnlyCompletedScribbles() async {
        mockRepo.scribblesToStream = [
            makeScribble(status: .completed),
            makeScribble(status: .completed),
            makeScribble(status: .pending),
            makeScribble(status: .failed)
        ]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [Scribble] = []
        for await batch in stream { result = batch; break }

        XCTAssertEqual(result.count, 2)
        XCTAssertTrue(result.allSatisfy { $0.status == .completed })
    }

    func test_execute_emptyRepository_returnsEmpty() async {
        mockRepo.scribblesToStream = []

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [Scribble] = []
        for await batch in stream { result = batch; break }

        XCTAssertTrue(result.isEmpty)
    }

    func test_execute_allPending_returnsEmpty() async {
        mockRepo.scribblesToStream = [
            makeScribble(status: .pending),
            makeScribble(status: .parsing)
        ]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [Scribble] = []
        for await batch in stream { result = batch; break }

        XCTAssertTrue(result.isEmpty)
    }

    func test_execute_completedAndSuccess_onlyCompletedReturned() async {
        mockRepo.scribblesToStream = [
            makeScribble(status: .completed),
            makeScribble(status: .success)  // success ≠ completed in ledger
        ]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [Scribble] = []
        for await batch in stream { result = batch; break }

        XCTAssertEqual(result.count, 1)
        XCTAssertEqual(result[0].status, .completed)
    }

    func test_execute_preservesScribbleData() async {
        let id = UUID()
        let date = Date(timeIntervalSince1970: 1_000_000)
        let rawText = "bench press 3x10 100kg"
        let scribble = Scribble(id: id, rawText: rawText, status: .completed, createdAt: date)
        mockRepo.scribblesToStream = [scribble]

        let stream = sut.execute(startDate: Date(timeIntervalSinceNow: -86400), endDate: Date())
        var result: [Scribble] = []
        for await batch in stream { result = batch; break }

        XCTAssertEqual(result[0].id, id)
        XCTAssertEqual(result[0].rawText, rawText)
        XCTAssertEqual(result[0].createdAt, date)
    }
}
