import SwiftUI

public struct ScribbleFitColors: Sendable {
    public let background: Color
    public let softGray: Color
    public let richBlack: Color
    public let midGray: Color
    public let lightGray: Color
    public let strongGray: Color
    public let dangerRed: Color
    public let successGreen: Color
    public let blue: Color
    public let scheme: ColorScheme

    public static func light() -> ScribbleFitColors {
        ScribbleFitColors(
            background: Color(hex: "FFFFFF"),
            softGray: Color(hex: "F7F7F8"),
            richBlack: Color(hex: "101010"),
            midGray: Color(hex: "8E8EA0"),
            lightGray: Color(hex: "E5E5EA"),
            strongGray: Color(hex: "636366"),
            dangerRed: Color(hex: "FF3B30"),
            successGreen: Color(hex: "34C759"),
            blue: Color(hex: "2B8CEE"),
            scheme: .light
        )
    }

    public static func dark() -> ScribbleFitColors {
        ScribbleFitColors(
            background: Color(hex: "000000"),
            softGray: Color(hex: "1A1A1A"),
            richBlack: Color(hex: "FFFFFF"),
            midGray: Color(hex: "8E8EA0"),
            lightGray: Color(hex: "2C2C2E"),
            strongGray: Color(hex: "636366"),
            dangerRed: Color(hex: "FF453A"),
            successGreen: Color(hex: "30D158"),
            blue: Color(hex: "2B8CEE"),
            scheme: .dark
        )
    }
}

private struct ScribbleFitColorsKey: EnvironmentKey {
    static let defaultValue = ScribbleFitColors.light()
}

extension EnvironmentValues {
    public var scribbleFitColors: ScribbleFitColors {
        get { self[ScribbleFitColorsKey.self] }
        set { self[ScribbleFitColorsKey.self] = newValue }
    }
}

public struct ScribbleFitTheme<Content: View>: View {
    @Environment(\.colorScheme) var colorScheme
    let content: () -> Content

    public init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content
    }

    public var body: some View {
        let colors = colorScheme == .dark ? ScribbleFitColors.dark() : ScribbleFitColors.light()
        content()
            .environment(\.scribbleFitColors, colors)
    }
}

extension View {
    public func scribbleFitTheme() -> some View {
        ScribbleFitTheme { self }
    }
}
