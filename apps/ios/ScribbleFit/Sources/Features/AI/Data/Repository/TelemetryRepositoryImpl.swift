import Foundation

public final class TelemetryRepositoryImpl: TelemetryRepository {
    private let networkClient: ScribbleFitNetworkClient
    
    public init(networkClient: ScribbleFitNetworkClient = .shared) {
        self.networkClient = networkClient
    }
    
    public func reportError(data: TelemetryData) async throws {
        let request = TelemetryRequest(
            rawText: data.rawText,
            promptVersion: data.promptVersion,
            errorMessage: data.errorMessage,
            errorCode: data.errorCode,
            deviceModel: data.deviceModel
        )
        try await networkClient.reportError(request: request)
    }
}
