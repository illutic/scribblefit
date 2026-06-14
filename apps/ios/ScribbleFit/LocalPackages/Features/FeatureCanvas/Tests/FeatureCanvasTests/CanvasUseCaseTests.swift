import XCTest
import CoreModel
import FeatureScribble
@testable import FeatureCanvas

// MARK: - Mocks (reused from FeatureScribble but scoped here)

@MainActor
private final class MockScribbleRepo: ScribbleRepository {
    var addedScribbles: [Scribble] = []
    var updatedScribbles: [Scribble] = []
    var deletedIds: [UUID] = []
    var clearedIds: [UUID] = []
    var confirmedScribbles: [Scribble] = []

    var scribbleToReturn: Scribble?
    var shouldThrowOnAdd: Error?
    var shouldThrowOnUpdate: Error?
    var shouldThrowOnDelete: Error?

    func observeScribbles(for date: Date) -> AsyncStream<[Scribble]> {
        AsyncStream { $0.finish() }
    }
    func observeScribbles(startDate: Date, endDate: Date) -> AsyncStream<[Scribble]> {
        AsyncStream { $0.finish() }
    }
    func observeScribblesWithExercise(exerciseName: String) -> AsyncStream<[Scribble]> {
        AsyncStream { $0.finish() }
    }
    func addScribble(_ scribble: Scribble) async throws {
        if let e = shouldThrowOnAdd { throw e }
        addedScribbles.append(scribble)
    }
    func updateScribble(_ scribble: Scribble) async throws {
        if let e = shouldThrowOnUpdate { throw e }
        updatedScribbles.append(scribble)
    }
    func deleteScribble(id: UUID) async throws {
        if let e = shouldThrowOnDelete { throw e }
        deletedIds.append(id)
    }
    func getScribble(id: UUID) async throws -> Scribble? { scribbleToReturn }
    func clearScribbleExercises(scribbleId: UUID) async throws { clearedIds.append(scribbleId) }
    func confirmScribble(_ scribble: Scribble) async throws {
        if let e = shouldThrowOnUpdate { throw e }
        confirmedScribbles.append(scribble)
    }
}

// MARK: - Helpers

private func makeScribble(
    id: UUID = UUID(),
    rawText: String = "bench 3x10",
    status: ScribbleStatus = .success,
    exercises: [Exercise] = []
) -> Scribble {
    Scribble(id: id, rawText: rawText, status: status, exercises: exercises)
}



// MARK: - AddRawScribbleUseCase Tests

@MainActor
final class AddRawScribbleUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepo!
    private var sut: AddRawScribbleUseCase!

    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockScribbleRepo()
        sut = AddRawScribbleUseCase(repository: mockRepo)
    }

    func test_execute_withValidText_addsScribble() async throws {
        try await sut.execute(text: "bench press 3x10 100kg", date: Date())
        XCTAssertEqual(mockRepo.addedScribbles.count, 1)
    }

    func test_execute_trimmedText_usedAsRawText() async throws {
        try await sut.execute(text: "  squat  ", date: Date())
        XCTAssertEqual(mockRepo.addedScribbles[0].rawText, "squat")
    }

    func test_execute_scribbleStatusIsPending() async throws {
        try await sut.execute(text: "deadlift 5x5", date: Date())
        XCTAssertEqual(mockRepo.addedScribbles[0].status, .pending)
    }

    func test_execute_scribbleDateIsPreserved() async throws {
        let date = Date(timeIntervalSince1970: 500_000)
        try await sut.execute(text: "row 3x10", date: date)
        XCTAssertEqual(mockRepo.addedScribbles[0].createdAt, date)
    }

    func test_execute_emptyText_doesNotAddScribble() async throws {
        try await sut.execute(text: "   ", date: Date())
        XCTAssertTrue(mockRepo.addedScribbles.isEmpty)
    }

    func test_execute_emptyStringText_doesNotAddScribble() async throws {
        try await sut.execute(text: "", date: Date())
        XCTAssertTrue(mockRepo.addedScribbles.isEmpty)
    }

    func test_execute_repositoryError_propagates() async {
        mockRepo.shouldThrowOnAdd = NSError(domain: "DB", code: 1)
        do { try await self.sut.execute(text: "valid", date: Date()); XCTFail("Expected error") } catch {}
    }
}

// MARK: - ConfirmScribbleUseCase Tests

@MainActor
final class ConfirmScribbleUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepo!
    private var sut: ConfirmScribbleUseCase!

    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockScribbleRepo()
        sut = ConfirmScribbleUseCase(scribbleRepository: mockRepo)
    }

    func test_execute_successStatus_confirmsScribble() async throws {
        let scribble = makeScribble(status: .success)
        try await sut.execute(scribble: scribble)
        XCTAssertEqual(mockRepo.confirmedScribbles.count, 1)
    }

    func test_execute_completedScribble_hasCompletedStatus() async throws {
        let scribble = makeScribble(status: .success)
        try await sut.execute(scribble: scribble)
        XCTAssertEqual(mockRepo.confirmedScribbles[0].status, .completed)
    }

    func test_execute_pendingStatus_throwsInvalidStatus() async {
        let scribble = makeScribble(status: .pending)
        do { try await self.sut.execute(scribble: scribble); XCTFail("Expected error") } catch {}
    }

    func test_execute_failedStatus_throwsInvalidStatus() async {
        let scribble = makeScribble(status: .failed)
        do { try await self.sut.execute(scribble: scribble); XCTFail("Expected error") } catch {}
    }

    func test_execute_alreadyCompleted_throwsInvalidStatus() async {
        let scribble = makeScribble(status: .completed)
        do { try await self.sut.execute(scribble: scribble); XCTFail("Expected error") } catch {}
    }

    func test_execute_repositoryError_propagates() async {
        let scribble = makeScribble(status: .success)
        mockRepo.shouldThrowOnUpdate = NSError(domain: "DB", code: 2)
        do { try await self.sut.execute(scribble: scribble); XCTFail("Expected error") } catch {}
    }
}

// MARK: - DeleteScribbleUseCase Tests

@MainActor
final class DeleteScribbleUseCaseTests: XCTestCase {

    private var mockRepo: MockScribbleRepo!
    private var removeUseCase: RemoveScribbleUseCase!
    private var sut: DeleteScribbleUseCase!

    override func setUp() async throws {
        try await super.setUp()
        mockRepo = MockScribbleRepo()
        removeUseCase = RemoveScribbleUseCase(repository: mockRepo)
        sut = DeleteScribbleUseCase(removeScribbleUseCase: removeUseCase)
    }

    func test_execute_delegatesToRemoveScribbleUseCase() async throws {
        let id = UUID()
        try await sut.execute(id: id)
        XCTAssertEqual(mockRepo.deletedIds, [id])
    }

    func test_execute_propagatesError() async {
        mockRepo.shouldThrowOnDelete = NSError(domain: "Test", code: 5)
        do { try await self.sut.execute(id: UUID()); XCTFail("Expected error") } catch {}
    }

    func test_execute_calledMultipleTimes_passesAllIds() async throws {
        let id1 = UUID()
        let id2 = UUID()
        try await sut.execute(id: id1)
        try await sut.execute(id: id2)
        XCTAssertEqual(mockRepo.deletedIds, [id1, id2])
    }
}
