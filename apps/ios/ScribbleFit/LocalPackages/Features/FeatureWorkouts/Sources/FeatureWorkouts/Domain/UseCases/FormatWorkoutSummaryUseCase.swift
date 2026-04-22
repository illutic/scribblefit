import Foundation
import CoreModel

public struct VolumeSummary: Sendable {
    public let value: String
    public let isKilo: Bool
    
    public init(value: String, isKilo: Bool) {
        self.value = value
        self.isKilo = isKilo
    }
}

public struct FormatWorkoutSummaryUseCase: Sendable {
    public init() {}
    
    public func execute(totalVolume: Double) -> VolumeSummary {
        if totalVolume >= 1000 {
            return VolumeSummary(
                value: String(format: "%.1f", totalVolume / 1000.0),
                isKilo: true
            )
        } else {
            return VolumeSummary(
                value: String(format: "%.0f", totalVolume),
                isKilo: false
            )
        }
    }
}
