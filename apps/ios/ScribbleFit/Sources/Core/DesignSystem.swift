import SwiftUI

public enum ScribbleFitColor {
    public static let background = Color(hex: 0xFFFFFF)
    public static let softGray = Color(hex: 0xF7F7F8)
    public static let richBlack = Color(hex: 0x101010)
    public static let midGray = Color(hex: 0x8E8EA0)
    public static let lightGray = Color(hex: 0xE5E5EA)
    public static let dangerRed = Color(hex: 0xFF3B30)
    public static let errorBackground = Color(hex: 0xFEE2E2)
    public static let errorText = Color(hex: 0x991B1B)
}

public struct ScribbleFitSpacing {
    public static let small: CGFloat = 8
    public static let medium: CGFloat = 16
    public static let large: CGFloat = 24
    public static let screenPadding: CGFloat = 24
}

public struct ScribbleFitCornerRadius {
    public static let small: CGFloat = 8
    public static let medium: CGFloat = 12
    public static let large: CGFloat = 20
}

private extension Color {
    init(hex: UInt32) {
        let r = Double((hex >> 16) & 0xFF) / 255
        let g = Double((hex >> 8) & 0xFF) / 255
        let b = Double(hex & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}

// MARK: - Reusable Components

public struct ScribbleFitCard<Content: View>: View {
    private let content: Content

    public init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    public var body: some View {
        content
            .padding(ScribbleFitSpacing.medium)
            .background(ScribbleFitColor.softGray)
            .clipShape(RoundedRectangle(cornerRadius: ScribbleFitCornerRadius.medium))
    }
}

public struct ScribbleFitPill: View {
    private let text: String

    public init(_ text: String) { self.text = text }

    public var body: some View {
        Text(text)
            .font(.system(size: 14))
            .foregroundStyle(ScribbleFitColor.richBlack)
            .padding(.horizontal, ScribbleFitSpacing.medium)
            .padding(.vertical, ScribbleFitSpacing.small)
            .background(ScribbleFitColor.softGray)
            .clipShape(Capsule())
    }
}
