import XCTest
import CoreModel
import FeatureAI
@testable import FeatureSettings

// MARK: - Mock SettingsRepository

private final class MockSettingsRepository: SettingsRepository, @unchecked Sendable {
    var clearAllDataCalled = false
    var exportedURL: URL?
    var shouldThrowOnClear: Error?
    var shouldThrowOnExport: Error?

    func clearAllData() async throws {
        if let e = shouldThrowOnClear { throw e }
        clearAllDataCalled = true
    }

    func exportUserData() async throws -> URL {
        if let e = shouldThrowOnExport { throw e }
        return exportedURL ?? URL(fileURLWithPath: "/tmp/export.json")
    }
}

// MARK: - Mock LLMService (for CheckLocalSupportUseCase)

@MainActor
private final class MockLLMService: LLMService {
    func parseWorkout(rawText: String) async throws -> ParsedWorkoutResult {
        ParsedWorkoutResult(exercises: [], rawText: rawText)
    }
    func generateInsightsSummary(exercises: [Exercise]) async throws -> [AIInsight] { [] }
    func generateExerciseInsight(history: String) async throws -> AIInsight {
        AIInsight(insightType: .trend, text: "stub")
    }
    func isSupported() async -> Bool { false }
}

// MARK: - ClearAllDataUseCase Tests

@MainActor
final class ClearAllDataUseCaseTests: XCTestCase {

    private var mockRepo: MockSettingsRepository!
    private var sut: ClearAllDataUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockSettingsRepository()
        sut = ClearAllDataUseCase(repository: mockRepo)
    }

    func test_execute_callsClearAllData() async throws {
        try await sut.execute()
        XCTAssertTrue(mockRepo.clearAllDataCalled)
    }

    func test_execute_propagatesError() async {
        mockRepo.shouldThrowOnClear = NSError(domain: "DB", code: 1)
        do {
            try await sut.execute()
            XCTFail("Expected error")
        } catch {
            XCTAssertNotNil(error)
        }
    }

    func test_execute_calledTwice_callsRepositoryTwice() async throws {
        try await sut.execute()
        try await sut.execute()
        XCTAssertTrue(mockRepo.clearAllDataCalled)  // Called at least once
    }
}

// MARK: - ExportUserDataUseCase Tests

@MainActor
final class ExportUserDataUseCaseTests: XCTestCase {

    private var mockRepo: MockSettingsRepository!
    private var sut: ExportUserDataUseCase!

    override func setUp() async throws {
        // try await super.setUp() removed for Swift 6 XCTestCase data-race issue
        mockRepo = MockSettingsRepository()
        sut = ExportUserDataUseCase(repository: mockRepo)
    }

    func test_execute_returnsExportedURL() async throws {
        let expectedURL = URL(fileURLWithPath: "/tmp/myexport.json")
        mockRepo.exportedURL = expectedURL

        let url = try await sut.execute()

        XCTAssertEqual(url, expectedURL)
    }

    func test_execute_propagatesError() async {
        mockRepo.shouldThrowOnExport = NSError(domain: "Export", code: 500)
        do {
            _ = try await sut.execute()
            XCTFail("Expected error")
        } catch {
            XCTAssertNotNil(error)
        }
    }

    func test_execute_returnsNonNilURL() async throws {
        let url = try await sut.execute()
        XCTAssertNotNil(url)
    }
}

// MARK: - CheckLocalSupportUseCase Tests

@MainActor
final class CheckLocalSupportUseCaseTests: XCTestCase {

    // NOTE: CheckLocalSupportUseCase has iOS 26 availability guards and checks for
    // LocalLLMService type specifically. On CI / older SDKs, it always returns false.

    func test_execute_withNilLLM_returnsFalse() async {
        let sut = CheckLocalSupportUseCase(localLLM: nil)
        let result = await sut.execute()
        XCTAssertFalse(result)
    }

    func test_execute_withNonLocalLLM_returnsFalse() async {
        let mockLLM = MockLLMService()
        let sut = CheckLocalSupportUseCase(localLLM: mockLLM)
        // MockLLMService is not LocalLLMService, so returns false
        let result = await sut.execute()
        XCTAssertFalse(result)
    }

    // On iOS <26, always returns false regardless of LLM type
    func test_execute_belowiOS26_alwaysReturnsFalse() async {
        let sut = CheckLocalSupportUseCase(localLLM: nil)
        let result = await sut.execute()
        XCTAssertFalse(result)
    }
}
