import SwiftUI

public struct ScribbleFitColors: Sendable {
    public let scheme: ColorScheme
    
    public var background: Color { Color.scribbleFitBackground(for: scheme) }
    public var softGray: Color { Color.scribbleFitSoftGray(for: scheme) }
    public var richBlack: Color { Color.scribbleFitRichBlack(for: scheme) }
    public var midGray: Color { Color.scribbleFitMidGray(for: scheme) }
    public var lightGray: Color { Color.scribbleFitLightGray(for: scheme) }
    public var strongGray: Color { Color.scribbleFitStrongGray(for: scheme) }
    public var dangerRed: Color { Color.scribbleFitDangerRed(for: scheme) }
    public var successGreen: Color { Color.scribbleFitSuccessGreen(for: scheme) }
    public var blue: Color { Color.scribbleFitBlue(for: scheme) }
}

public struct ScribbleFitTheme {
    public static func colors(for scheme: ColorScheme) -> ScribbleFitColors {
        ScribbleFitColors(scheme: scheme)
    }
}

// Environment Key for easier access
private struct ScribbleFitColorsKey: EnvironmentKey {
    static let defaultValue = ScribbleFitColors(scheme: .light)
}

extension EnvironmentValues {
    public var scribbleFitColors: ScribbleFitColors {
        get { self[ScribbleFitColorsKey.self] }
        set { self[ScribbleFitColorsKey.self] = newValue }
    }
}

public struct ScribbleFitThemeProvider<Content: View>: View {
    @Environment(\.colorScheme) var scheme
    let content: Content
    
    public init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }
    
    public var body: some View {
        content
            .environment(\.scribbleFitColors, ScribbleFitColors(scheme: scheme))
    }
}
