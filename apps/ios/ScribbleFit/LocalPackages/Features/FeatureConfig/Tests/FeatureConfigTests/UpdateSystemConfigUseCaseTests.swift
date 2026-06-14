import XCTest
import Combine
import CoreModel
@testable import FeatureConfig

// MARK: - Mock

@MainActor
final class MockConfigRepository: ConfigRepository {
    var updatedConfig: SystemConfig?
    var configToReturn: SystemConfig = SystemConfig()

    private let subject = PassthroughSubject<SystemConfig, Never>()

    var configPublisher: AnyPublisher<SystemConfig, Never> {
        subject.eraseToAnyPublisher()
    }

    func getConfig() -> SystemConfig { configToReturn }

    func updateConfig(_ config: SystemConfig) {
        updatedConfig = config
        subject.send(config)
    }

    func resetConfig() {
        updatedConfig = nil
    }

    func fetchRemoteConfig() async throws {}
}

// MARK: - Tests

@MainActor
final class UpdateSystemConfigUseCaseTests: XCTestCase {

    private var mockRepository: MockConfigRepository!
    private var sut: UpdateSystemConfigUseCase!

    override func setUp() async throws {
        try await super.setUp()
        mockRepository = MockConfigRepository()
        sut = UpdateSystemConfigUseCase(repository: mockRepository)
    }

    override func tearDown() async throws {
        sut = nil
        mockRepository = nil
        try await super.tearDown()
    }

    // MARK: - Happy Path

    func test_execute_callsRepositoryWithConfig() {
        let config = SystemConfig(weightUnit: .lbs)

        sut.execute(config: config)

        XCTAssertEqual(mockRepository.updatedConfig, config)
    }

    func test_execute_defaultConfig_passesThroughUnchanged() {
        let config = SystemConfig()

        sut.execute(config: config)

        XCTAssertEqual(mockRepository.updatedConfig?.weightUnit, .kgs)
        XCTAssertEqual(mockRepository.updatedConfig?.preferredLlmProvider, .local)
    }

    func test_execute_customTheme_preserved() {
        let config = SystemConfig(themePreference: .dark, isDynamicTheme: true)

        sut.execute(config: config)

        XCTAssertEqual(mockRepository.updatedConfig?.themePreference, .dark)
        XCTAssertEqual(mockRepository.updatedConfig?.isDynamicTheme, true)
    }

    func test_execute_lbsWeightUnit_preserved() {
        let config = SystemConfig(weightUnit: .lbs)

        sut.execute(config: config)

        XCTAssertEqual(mockRepository.updatedConfig?.weightUnit, .lbs)
    }

    // MARK: - Multiple Calls

    func test_execute_calledTwice_lastConfigWins() {
        let config1 = SystemConfig(weightUnit: .lbs)
        let config2 = SystemConfig(weightUnit: .kgs)

        sut.execute(config: config1)
        sut.execute(config: config2)

        XCTAssertEqual(mockRepository.updatedConfig?.weightUnit, .kgs)
    }
}
