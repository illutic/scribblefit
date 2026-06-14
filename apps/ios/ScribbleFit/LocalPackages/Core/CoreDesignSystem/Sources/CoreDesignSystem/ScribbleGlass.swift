import SwiftUI

public struct ScribbleGlassModifier: ViewModifier {
    public let cornerRadius: CGFloat

    public init(cornerRadius: CGFloat = 16) {
        self.cornerRadius = cornerRadius
    }

    @MainActor
    public func body(content: Content) -> some View {
        let shape = RoundedRectangle(cornerRadius: cornerRadius)

        if #available(iOS 26.0, macOS 26.0, *) {
            // Using the shim to avoid compile-time errors on older SDKs
            content.glassEffectShim(in: shape)
        } else {
            content
                .background(.ultraThinMaterial, in: shape)
                .overlay(
                    shape
                        .stroke(Color.white.opacity(0.15), lineWidth: 0.5)
                )
        }
    }
}

private extension View {
    @ViewBuilder
    func glassEffectShim(in shape: some Shape) -> some View {
#if compiler(>=10.0)
        if #available(iOS 26.0, macOS 26.0, *) {
            self.glassEffect(in: shape)
        } else {
            self.background(.ultraThinMaterial, in: shape)
        }
#else
        self.background(.ultraThinMaterial, in: shape)
#endif
    }
}

public extension View {
    @MainActor
    func scribbleGlass(cornerRadius: CGFloat = 16) -> some View {
        self.modifier(ScribbleGlassModifier(cornerRadius: cornerRadius))
    }
}
