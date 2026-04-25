import Foundation

public enum TrendDirection: String, Codable, Sendable {
    case improving = "IMPROVING"
    case stable = "STABLE"
    case plateaued = "PLATEAUED"
    case declining = "DECLINING"
}
