import Foundation

public enum InsightsIntent: Sendable {
    case refresh
    case selectPeriod(InsightsPeriod)
}
